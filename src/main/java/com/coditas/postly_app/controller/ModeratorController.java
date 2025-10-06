package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.service.ModeratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;

    // Get all posts pending review
    @GetMapping("/posts/status/{status}")
    public ResponseEntity<List<PostDto>> getPostByStatus(@PathVariable String status) {
        String statusInUpperCase = status.toUpperCase();
        return ResponseEntity.ok(moderatorService.getPostsByStatus(Post.Status.valueOf(statusInUpperCase)));
    }

    // Approve or reject a post
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDto> reviewPost(
            @PathVariable Long postId,
            @RequestBody ModeratorActionDto action // APPROVE or REJECT
    ) {
        return ResponseEntity.ok(moderatorService.reviewPost(postId, action));
    }

    // Get all comments pending review
    @GetMapping("/comments/status/{status}")
    public ResponseEntity<List<CommentDto>> getCommentsByStatus(@PathVariable String status) {
        String statusInUpperCase = status.toUpperCase();
        return ResponseEntity.ok(moderatorService.getCommentsByStatus(Comment.Status.valueOf(statusInUpperCase)));
    }

    // Approve or reject a comment
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> reviewComment(
            @PathVariable Long commentId,
            @RequestBody ModeratorActionDto action // APPROVE or REJECT
    ) {
        return ResponseEntity.ok(moderatorService.reviewComment(commentId, action));
    }
}

