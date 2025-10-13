package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.service.CommentService;
import com.coditas.postly_app.service.PostService;
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
    public ResponseEntity<ApiResponseDto<List<PostDto>>> getPostByStatus(@PathVariable String status) {
        String statusInUpperCase = status.toUpperCase();

        List<PostDto> data = postService.getPostsByStatus(Post.Status.valueOf(statusInUpperCase));

        ApiResponseDto<List<PostDto>> responseBody = new ApiResponseDto<>(true, "Posts fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    // Approve or reject a post
    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<PostDto>> reviewPost(
            @PathVariable Long postId,
            @RequestBody ModeratorActionDto action // APPROVE or REJECT
    ) {

        PostDto data = postService.reviewPost(postId, action);
        ApiResponseDto<PostDto> responseBody = new ApiResponseDto<>(true, "Status updated", data);

        return ResponseEntity.ok(responseBody);
    }

    // Get all comments pending review
    @GetMapping("/comments/status/{status}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<CommentDto>>> getCommentsByStatus(@PathVariable String status) {
        String statusInUpperCase = status.toUpperCase();

        List<CommentDto> data = commentService.getCommentsByStatus(Comment.Status.valueOf(statusInUpperCase));

        ApiResponseDto<List<CommentDto>> responseBody = new ApiResponseDto<>(true, "Comments fetched",data);

        return ResponseEntity.ok(responseBody);
    }

    // Approve or reject a comment
    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<CommentDto>> reviewComment(
            @PathVariable Long commentId,
            @RequestBody ModeratorActionDto action // APPROVE or REJECT
    ) {

        CommentDto data = commentService.reviewComment(commentId, action);

        ApiResponseDto<CommentDto> responseBody = new ApiResponseDto<>(true, "Comment status updated", data);

        return ResponseEntity.ok(responseBody);
    }
}

