package com.example.multiblogservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:#{null}}")
    private String databaseUrl;

    @Value("${spring.datasource.url:#{null}}")
    private String localDatabaseUrl;

    @Value("${spring.datasource.username:#{null}}")
    private String localUsername;

    @Value("${spring.datasource.password:#{null}}")
    private String localPassword;

    @Bean
    @Profile("cloud")
    public DataSource cloudDataSource() {
        String[] parts = databaseUrl.split("@");
        String[] credentials = parts[0].split(":");
        String[] hostParts = parts[1].split("/");

        String username = credentials[1].substring(2); // Remove the '//' at the beginning
        String password = credentials[2];
        String host = hostParts[0];
        String database = hostParts[1].split("\\?")[0];

        String jdbcUrl = String.format("jdbc:postgresql://%s/%s?sslmode=disable", host, database);

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    @Profile("local")
    public DataSource localDataSource() {
        return DataSourceBuilder.create()
                .url(localDatabaseUrl)
                .username(localUsername)
                .password(localPassword)
                .build();
    }
}
