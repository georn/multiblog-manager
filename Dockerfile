# Use the official OpenJDK base image
FROM openjdk:21-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY multi-blog-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
CMD ["java", "-Xmx300m", "-Xms300m", "-jar", "-Dspring.profiles.active=cloud", "-Dserver.address=0.0.0.0", "-Dserver.port=8080", "app.jar"]
