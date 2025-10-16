package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.CommentResponseDto;
import com.coditas.postly_app.dto.CommentCreateRequestDto;
import com.coditas.postly_app.dto.CommentUpdateRequestDto;
import com.coditas.postly_app.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> addComment(@RequestBody @Valid CommentCreateRequestDto request) {

        CommentResponseDto data = commentService.addComment(request);

        ApiResponseDto<CommentResponseDto> responseBody = new ApiResponseDto<>(true, "Comment created successfully", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> getCommentsByPost(@PathVariable Long postId) {

        List<CommentResponseDto> data = commentService.getCommentsByPost(postId);
        ApiResponseDto<List<CommentResponseDto>> responseBody = new ApiResponseDto<>(true, "Comment fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> updateComment(@PathVariable Long commentId, @RequestBody @Valid CommentUpdateRequestDto request) {

        CommentResponseDto data = commentService.updateComment(commentId, request);
        ApiResponseDto<CommentResponseDto> responseBody = new ApiResponseDto<>(true, "Comment Updated", data);

        return ResponseEntity.ok(responseBody);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

