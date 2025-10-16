package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.PostResponseDto;
import com.coditas.postly_app.dto.PostWithCommentResponseDto;
import com.coditas.postly_app.dto.PostCreateRequestDto;
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
    public ResponseEntity<ApiResponseDto<PostResponseDto>> createPost(@RequestBody @Valid PostCreateRequestDto request) {

        PostResponseDto data = postService.createPost(request);

        ApiResponseDto<PostResponseDto> responseBody = new ApiResponseDto<>(true, "Post Created", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("status/approved")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<PostWithCommentResponseDto>>> getAllApprovedPosts() {

        List<PostWithCommentResponseDto> data = postService.getAllApprovedPosts();

        ApiResponseDto<List<PostWithCommentResponseDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> getPostById(@PathVariable Long postId) {

        PostResponseDto data = postService.getPostById(postId);

        ApiResponseDto<PostResponseDto> responseBody = new ApiResponseDto<>(true, "Post fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostByUserId(@PathVariable Long userId) {

        List<PostResponseDto> data = postService.getPostsByUser(userId);

        ApiResponseDto<List<PostResponseDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/user/{id}/status/{status}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostByUserIdAndStatus(@PathVariable Long id, @PathVariable String status) {

        List<PostResponseDto> data = postService.getPostsByUserAndStatus(id, status);

        ApiResponseDto<List<PostResponseDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> updatePost(@PathVariable Long id, @RequestBody PostCreateRequestDto request) {

        PostResponseDto data = postService.updatePost(id, request);

        ApiResponseDto<PostResponseDto> responseBody = new ApiResponseDto<>(true, "Post Updated", data);

        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}

