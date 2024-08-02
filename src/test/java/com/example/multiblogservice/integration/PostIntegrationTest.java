package com.example.multiblogservice.integration;

import com.example.multiblogservice.model.Blog;
import com.example.multiblogservice.model.Post;
import com.example.multiblogservice.repository.BlogRepository;
import com.example.multiblogservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PostIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Test
    public void whenFindByBlogId_thenReturnPosts() {
        // given
        Blog blog = new Blog();
        blog.setName("Test Blog");
        blog.setDescription("Test Description");
        blog = blogRepository.save(blog);

        Post post1 = new Post();
        post1.setTitle("Test Post 1");
        post1.setContent("Test Content 1");
        post1.setBlog(blog);
        entityManager.persist(post1);

        Post post2 = new Post();
        post2.setTitle("Test Post 2");
        post2.setContent("Test Content 2");
        post2.setBlog(blog);
        entityManager.persist(post2);

        entityManager.flush();

        // when
        List<Post> found = postRepository.findByBlogId(blog.getId());

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Post::getTitle).containsExactlyInAnyOrder("Test Post 1", "Test Post 2");
    }
}
