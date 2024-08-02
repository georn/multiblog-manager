package com.example.multiblogservice.service;

import com.example.multiblogservice.dto.BlogDTO;
import com.example.multiblogservice.model.Blog;
import com.example.multiblogservice.repository.BlogRepository;
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
@DisplayName("Blog Service Tests")
public class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private BlogService blogService;

    private Blog testBlog;

    @BeforeEach
    void setUp() {
        testBlog = new Blog();
        testBlog.setId(1L);
        testBlog.setName("Test Blog");
        testBlog.setDescription("This is a test blog");
        testBlog.setCreatedAt(LocalDateTime.now());
        testBlog.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("getAllBlogs should return a list of BlogDTOs")
    void whenGetAllBlogs_thenReturnBlogDTOList() {
        when(blogRepository.findAll()).thenReturn(List.of(testBlog));

        List<BlogDTO> result = blogService.getAllBlogs();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Blog", result.get(0).getName());
        assertEquals("This is a test blog", result.get(0).getDescription());
    }

    @Test
    @DisplayName("getBlogById should return a BlogDTO when blog exists")
    void whenGetBlogById_thenReturnBlogDTO() {
        when(blogRepository.findById(1L)).thenReturn(Optional.of(testBlog));

        Optional<BlogDTO> result = blogService.getBlogById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Blog", result.get().getName());
        assertEquals("This is a test blog", result.get().getDescription());
    }

    @Test
    @DisplayName("getBlogById should return empty when blog doesn't exist")
    void whenGetBlogByNonExistentId_thenReturnEmpty() {
        when(blogRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<BlogDTO> result = blogService.getBlogById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("createBlog should return a BlogDTO of the created blog")
    void whenCreateBlog_thenReturnBlogDTO() {
        when(blogRepository.save(any(Blog.class))).thenReturn(testBlog);

        BlogDTO result = blogService.createBlog(testBlog);

        assertNotNull(result);
        assertEquals("Test Blog", result.getName());
        assertEquals("This is a test blog", result.getDescription());
    }

    @Test
    @DisplayName("updateBlog should return an updated BlogDTO")
    void whenUpdateBlog_thenReturnUpdatedBlogDTO() {
        Blog updatedBlog = new Blog();
        updatedBlog.setName("Updated Blog");
        updatedBlog.setDescription("This is an updated blog");

        when(blogRepository.findById(1L)).thenReturn(Optional.of(testBlog));
        when(blogRepository.save(any(Blog.class))).thenReturn(updatedBlog);

        Optional<BlogDTO> result = blogService.updateBlog(1L, updatedBlog);

        assertTrue(result.isPresent());
        assertEquals("Updated Blog", result.get().getName());
        assertEquals("This is an updated blog", result.get().getDescription());
    }

    @Test
    @DisplayName("deleteBlog should call repository's deleteById method")
    void whenDeleteBlog_thenVerifyRepositoryCall() {
        blogService.deleteBlog(1L);

        verify(blogRepository, times(1)).deleteById(1L);
    }
}
