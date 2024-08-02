package com.example.multiblogservice.integration;

import com.example.multiblogservice.model.Blog;
import com.example.multiblogservice.repository.BlogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BlogIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BlogRepository blogRepository;

    @Test
    public void whenFindByName_thenReturnBlog() {
        // given
        Blog blog = new Blog();
        blog.setName("Test Blog");
        blog.setDescription("Test Description");
        entityManager.persist(blog);
        entityManager.flush();

        // when
        Optional<Blog> foundOptional = blogRepository.findByName(blog.getName());

        // then
        assertThat(foundOptional).isPresent();
        Blog found = foundOptional.get();
        assertThat(found.getName()).isEqualTo(blog.getName());
        assertThat(found.getDescription()).isEqualTo(blog.getDescription());
    }
}
