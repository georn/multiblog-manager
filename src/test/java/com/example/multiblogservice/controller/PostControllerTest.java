package com.example.multiblogservice.controller;

import com.example.multiblogservice.dto.PostDTO;
import com.example.multiblogservice.exception.ResourceNotFoundException;
import com.example.multiblogservice.model.Post;
import com.example.multiblogservice.service.PostService;
import com.example.multiblogservice.service.BlogService;
import com.example.multiblogservice.repository.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@DisplayName("Post Controller Tests")
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private BlogService blogService;

    @MockBean
    private BlogRepository blogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PostDTO testPostDTO;

    @BeforeEach
    void setUp() {
        testPostDTO = new PostDTO();
        testPostDTO.setId(1L);
        testPostDTO.setTitle("Test Post");
        testPostDTO.setContent("Test Content");
    }

    @Test
    @DisplayName("GET /api/v1/blogs/{blogId}/posts should return all posts for a blog")
    void whenGetAllPostsByBlogId_thenReturnJsonArray() throws Exception {
        when(postService.getAllPostsByBlogId(1L)).thenReturn(Arrays.asList(testPostDTO));

        mockMvc.perform(get("/api/v1/blogs/1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Post"))
                .andExpect(jsonPath("$[0].content").value("Test Content"));
    }

    @Test
    @DisplayName("GET /api/v1/blogs/{blogId}/posts/{id} should return a post when it exists")
    void whenGetPostById_thenReturnJson() throws Exception {
        when(postService.getPostById(1L)).thenReturn(Optional.of(testPostDTO));

        mockMvc.perform(get("/api/v1/blogs/1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    @DisplayName("GET /api/v1/blogs/{blogId}/posts/{id} should return 404 when post doesn't exist")
    void whenGetNonExistentPostById_thenReturn404() throws Exception {
        when(postService.getPostById(99L)).thenThrow(new ResourceNotFoundException("Post not found with id 99"));

        mockMvc.perform(get("/api/v1/blogs/1/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found with id 99"));
    }

    @Test
    @DisplayName("POST /api/v1/blogs/{blogId}/posts should create a new post and return it")
    void whenCreatePost_thenReturnJsonAndStatus200() throws Exception {
        Post postToCreate = new Post();
        postToCreate.setTitle("New Post");
        postToCreate.setContent("New Content");

        when(blogRepository.findById(1L)).thenReturn(Optional.of(new com.example.multiblogservice.model.Blog()));
        when(postService.createPost(any(Post.class))).thenReturn(testPostDTO);

        mockMvc.perform(post("/api/v1/blogs/1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    @DisplayName("PUT /api/v1/blogs/{blogId}/posts/{id} should update an existing post and return it")
    void whenUpdatePost_thenReturnJsonAndStatus200() throws Exception {
        Post postToUpdate = new Post();
        postToUpdate.setTitle("Updated Post");
        postToUpdate.setContent("Updated Content");

        when(postService.updatePost(eq(1L), any(Post.class))).thenReturn(Optional.of(testPostDTO));

        mockMvc.perform(put("/api/v1/blogs/1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    @DisplayName("DELETE /api/v1/blogs/{blogId}/posts/{id} should delete a post and return 204 No Content")
    void whenDeletePost_thenStatus204() throws Exception {
        when(postService.getPostById(1L)).thenReturn(Optional.of(testPostDTO));
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/v1/blogs/1/posts/1"))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).deletePost(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/blogs/{blogId}/posts/{id} should return 404 when post doesn't exist")
    void whenDeleteNonExistentPost_thenStatus404() throws Exception {
        when(postService.getPostById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/v1/blogs/1/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found with id 99"));

        verify(postService, never()).deletePost(anyLong());
    }

    @Test
    @DisplayName("POST /api/v1/blogs/{blogId}/posts should return 404 when blog doesn't exist")
    void whenCreatePostForNonExistentBlog_thenReturn404() throws Exception {
        Post postToCreate = new Post();
        postToCreate.setTitle("New Post");
        postToCreate.setContent("New Content");

        when(blogRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/blogs/99/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postToCreate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Blog not found with id 99"));
    }
}
