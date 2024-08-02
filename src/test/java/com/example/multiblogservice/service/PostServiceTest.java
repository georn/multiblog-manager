package com.example.multiblogservice.service;

import com.example.multiblogservice.dto.PostDTO;
import com.example.multiblogservice.exception.ResourceNotFoundException;
import com.example.multiblogservice.model.Blog;
import com.example.multiblogservice.model.Post;
import com.example.multiblogservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service Tests")
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private Blog testBlog;

    @BeforeEach
    void setUp() {
        testBlog = new Blog();
        testBlog.setId(1L);
        testBlog.setName("Test Blog");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("This is a test post content");
        testPost.setBlog(testBlog);
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("getAllPostsByBlogId should return a list of PostDTOs")
    void whenGetAllPostsByBlogId_thenReturnPostDTOList() {
        when(postRepository.findByBlogId(1L)).thenReturn(List.of(testPost));

        List<PostDTO> result = postService.getAllPostsByBlogId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Post", result.get(0).getTitle());
        assertEquals("This is a test post content", result.get(0).getContent());
    }

    @Test
    @DisplayName("getPostById should return a PostDTO when post exists")
    void whenGetPostById_thenReturnPostDTO() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        Optional<PostDTO> result = postService.getPostById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Post", result.get().getTitle());
        assertEquals("This is a test post content", result.get().getContent());
    }

    @Test
    @DisplayName("getPostById should return empty when post doesn't exist")
    void whenGetPostByNonExistentId_thenReturnEmpty() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PostDTO> result = postService.getPostById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("createPost should return a PostDTO of the created post")
    void whenCreatePost_thenReturnPostDTO() {
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        PostDTO result = postService.createPost(testPost);

        assertNotNull(result);
        assertEquals("Test Post", result.getTitle());
        assertEquals("This is a test post content", result.getContent());
    }

    @Test
    @DisplayName("updatePost should return an updated PostDTO")
    void whenUpdatePost_thenReturnUpdatedPostDTO() {
        Post updatedPost = new Post();
        updatedPost.setTitle("Updated Post");
        updatedPost.setContent("This is an updated post content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

        Optional<PostDTO> result = postService.updatePost(1L, updatedPost);

        assertTrue(result.isPresent());
        assertEquals("Updated Post", result.get().getTitle());
        assertEquals("This is an updated post content", result.get().getContent());
    }

    @Test
    @DisplayName("deletePost should call repository's delete method")
    void whenDeletePost_thenVerifyRepositoryCall() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.deletePost(1L);

        verify(postRepository, times(1)).delete(testPost);
    }

    @Test
    @DisplayName("deletePost should throw ResourceNotFoundException when post doesn't exist")
    void whenDeleteNonExistentPost_thenThrowException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(99L));

        verify(postRepository, never()).delete(any(Post.class));
    }
}
