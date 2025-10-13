package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;
import com.coditas.postly_app.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponseDto<String>> createPost(@RequestBody @Valid PostRequestDto request) {

        String data = postService.createPost(request);

        ApiResponseDto<String> responseBody = new ApiResponseDto<>(true, "Post Created", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("status/approved")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<PostDto>>> getAllApprovedPosts() {

        List<PostDto> data = postService.getAllApprovedPosts();

        ApiResponseDto<List<PostDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<PostDto>> getPostById(@PathVariable Long postId) {

        PostDto data = postService.getPostById(postId);

        ApiResponseDto<PostDto> responseBody = new ApiResponseDto<>(true, "Post fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<PostDto>>> getPostByUserId(@PathVariable Long userId) {

        List<PostDto> data = postService.getPostsByUser(userId);

        ApiResponseDto<List<PostDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/user/{id}/status/{status}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<PostDto>>> getPostUserIdAndStatus(@PathVariable Long id, @PathVariable String status) {

        List<PostDto> data = postService.getPostsByUserAndStatus(id, status);

        ApiResponseDto<List<PostDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponseDto<PostDto>> updatePost(@PathVariable Long id, @RequestBody PostRequestDto request) {

        PostDto data = postService.updatePost(id, request);

        ApiResponseDto<PostDto> responseBody = new ApiResponseDto<>(true, "Post Updated", data);

        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}

