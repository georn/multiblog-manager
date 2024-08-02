package com.example.multiblogservice.controller;

import com.example.multiblogservice.dto.PostDTO;
import com.example.multiblogservice.exception.ResourceNotFoundException;
import com.example.multiblogservice.model.Post;
import com.example.multiblogservice.repository.BlogRepository;
import com.example.multiblogservice.service.BlogService;
import com.example.multiblogservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs/{blogId}/posts")
public class PostController {

    private final PostService postService;
    private final BlogService blogService;
    private final BlogRepository blogRepository;

    @Autowired
    public PostController(PostService postService, BlogService blogService, BlogRepository blogRepository) {
        this.postService = postService;
        this.blogService = blogService;
        this.blogRepository = blogRepository;
    }

    @GetMapping
    public List<PostDTO> getAllPostsByBlogId(@PathVariable Long blogId) {
        return postService.getAllPostsByBlogId(blogId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@PathVariable Long blogId, @RequestBody Post post) {
        return blogRepository.findById(blogId)
                .map(blog -> {
                    post.setBlog(blog);
                    PostDTO savedPost = postService.createPost(post);
                    return ResponseEntity.ok(savedPost);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id " + blogId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        return postService.updatePost(id, postDetails)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(post -> {
                    postService.deletePost(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));
    }
}
