package com.example.multiblogservice.service;

import com.example.multiblogservice.dto.BlogDTO;
import com.example.multiblogservice.dto.PostDTO;
import com.example.multiblogservice.model.Blog;
import com.example.multiblogservice.model.Post;
import com.example.multiblogservice.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public List<BlogDTO> getAllBlogs() {
        return blogRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BlogDTO> getBlogById(Long id) {
        return blogRepository.findById(id).map(this::convertToDTO);
    }

    public BlogDTO createBlog(Blog blog) {
        return convertToDTO(blogRepository.save(blog));
    }

    public Optional<BlogDTO> updateBlog(Long id, Blog blogDetails) {
        return blogRepository.findById(id)
                .map(existingBlog -> {
                    existingBlog.setName(blogDetails.getName());
                    existingBlog.setDescription(blogDetails.getDescription());
                    return convertToDTO(blogRepository.save(existingBlog));
                });
    }

    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    private BlogDTO convertToDTO(Blog blog) {
        BlogDTO dto = new BlogDTO();
        dto.setId(blog.getId());
        dto.setName(blog.getName());
        dto.setDescription(blog.getDescription());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        dto.setPosts(blog.getPosts().stream()
                .map(this::convertToPostDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private PostDTO convertToPostDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}
