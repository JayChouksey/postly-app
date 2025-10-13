package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.CommentRequestDto;
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
    public ResponseEntity<ApiResponseDto<CommentDto>> addComment(@RequestBody @Valid CommentRequestDto request) {

        CommentDto data = commentService.addComment(request);

        ApiResponseDto<CommentDto> responseBody = new ApiResponseDto<>(true, "Comment created successfully", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<CommentDto>>> getCommentsByPost(@PathVariable Long postId) {

        List<CommentDto> data = commentService.getCommentsByPost(postId);
        ApiResponseDto<List<CommentDto>> responseBody = new ApiResponseDto<>(true, "Comment fetched", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponseDto<CommentDto>> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto request) {

        CommentDto data = commentService.updateComment(id, request);
        ApiResponseDto<CommentDto> responseBody = new ApiResponseDto<>(true, "Comment Updated", data);

        return ResponseEntity.ok(responseBody);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

