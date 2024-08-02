package com.example.multiblogservice.controller;

import com.example.multiblogservice.dto.BlogDTO;
import com.example.multiblogservice.model.Blog;
import com.example.multiblogservice.service.BlogService;
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

@WebMvcTest(BlogController.class)
@DisplayName("Blog Controller Tests")
public class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogService blogService;

    @Autowired
    private ObjectMapper objectMapper;

    private BlogDTO testBlogDTO;

    @BeforeEach
    void setUp() {
        testBlogDTO = new BlogDTO();
        testBlogDTO.setId(1L);
        testBlogDTO.setName("Test Blog");
        testBlogDTO.setDescription("Test Description");
    }

    @Test
    @DisplayName("GET /api/v1/blogs should return all blogs")
    void whenGetAllBlogs_thenReturnJsonArray() throws Exception {
        when(blogService.getAllBlogs()).thenReturn(Arrays.asList(testBlogDTO));

        mockMvc.perform(get("/api/v1/blogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Blog"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @Test
    @DisplayName("GET /api/v1/blogs/{id} should return a blog when it exists")
    void whenGetBlogById_thenReturnJson() throws Exception {
        when(blogService.getBlogById(1L)).thenReturn(Optional.of(testBlogDTO));

        mockMvc.perform(get("/api/v1/blogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Blog"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @DisplayName("GET /api/v1/blogs/{id} should return 404 when blog doesn't exist")
    void whenGetNonExistentBlogById_thenReturn404() throws Exception {
        when(blogService.getBlogById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/blogs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/blogs should create a new blog and return it")
    void whenCreateBlog_thenReturnJsonAndStatus201() throws Exception {
        Blog blogToCreate = new Blog();
        blogToCreate.setName("New Blog");
        blogToCreate.setDescription("New Description");

        when(blogService.createBlog(any(Blog.class))).thenReturn(testBlogDTO);

        mockMvc.perform(post("/api/v1/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Blog"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @DisplayName("PUT /api/v1/blogs/{id} should update an existing blog and return it")
    void whenUpdateBlog_thenReturnJsonAndStatus200() throws Exception {
        Blog blogToUpdate = new Blog();
        blogToUpdate.setName("Updated Blog");
        blogToUpdate.setDescription("Updated Description");

        when(blogService.updateBlog(eq(1L), any(Blog.class))).thenReturn(Optional.of(testBlogDTO));

        mockMvc.perform(put("/api/v1/blogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Blog"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @DisplayName("DELETE /api/v1/blogs/{id} should delete a blog and return 204 No Content")
    void whenDeleteBlog_thenStatus204() throws Exception {
        doNothing().when(blogService).deleteBlog(1L);

        mockMvc.perform(delete("/api/v1/blogs/1"))
                .andExpect(status().isNoContent());

        verify(blogService, times(1)).deleteBlog(1L);
    }
}
