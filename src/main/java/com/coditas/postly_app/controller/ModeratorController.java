package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.service.CommentService;
import com.coditas.postly_app.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class ModeratorController {

    private final PostService postService;
    private final CommentService commentService;

    // Get all posts pending review
    @GetMapping("/posts/status/{status}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostByStatus(@PathVariable String status) {
        String statusInUpperCase = status.toUpperCase();

        List<PostResponseDto> data = postService.getPostsByStatus(Post.Status.valueOf(statusInUpperCase));

        ApiResponseDto<List<PostResponseDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    // Approve or reject a post
    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> reviewPost(
            @PathVariable Long postId,
            @RequestBody @Valid ActionRequestDto action // APPROVE or REJECT
    ) {

        PostResponseDto data = postService.reviewPost(postId, action);
        ApiResponseDto<PostResponseDto> responseBody = new ApiResponseDto<>(true, "Status updated", data);

        return ResponseEntity.ok(responseBody);
    }

    // Get all comments pending review
    @GetMapping("/comments/status/{status}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> getCommentsByStatus(@PathVariable String status) {
        String statusInUpperCase = status.toUpperCase();

        List<CommentResponseDto> data = commentService.getCommentsByStatus(Comment.Status.valueOf(statusInUpperCase));

        ApiResponseDto<List<CommentResponseDto>> responseBody = new ApiResponseDto<>(true, "Comments fetched",data);

        return ResponseEntity.ok(responseBody);
    }

    // Approve or reject a comment
    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> reviewComment(
            @PathVariable Long commentId,
            @RequestBody @Valid ActionRequestDto action // APPROVE or REJECT
    ) {

        CommentResponseDto data = commentService.reviewComment(commentId, action);

        ApiResponseDto<CommentResponseDto> responseBody = new ApiResponseDto<>(true, "Comment status updated", data);

        return ResponseEntity.ok(responseBody);
    }
}

