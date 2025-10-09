package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;
import com.coditas.postly_app.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequestDto request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<PostDto>> getAllApprovedPosts() {
        return ResponseEntity.ok(postService.getAllApprovedPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PostDto>> getPostByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostsByUser(id));
    }

    @GetMapping("/user/{id}/status/{status}")
    public ResponseEntity<List<PostDto>> getPostUserIdAndStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok(postService.getPostsByUserAndStatus(id, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostRequestDto request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}

