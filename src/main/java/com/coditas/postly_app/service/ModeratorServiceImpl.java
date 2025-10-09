package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.ReviewLog;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.CommentRepository;
import com.coditas.postly_app.repository.PostRepository;
import com.coditas.postly_app.repository.ReviewLogRepository;
import com.coditas.postly_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModeratorServiceImpl implements ModeratorService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final UserRepository userRepository;

    @Override
    public List<PostDto> getPostsByStatus(Post.Status status) {
        return postRepository.findByStatus(status)
                .stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto reviewPost(Long postId, ModeratorActionDto action) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String reviewerEmail = auth.getName();
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new CustomException("Reviewer not found", HttpStatus.NOT_FOUND));

        // Prevent reviewing own post
        if (post.getAuthor().getId().equals(reviewer.getId())) {
            throw new CustomException("You cannot review your own post", HttpStatus.FORBIDDEN);
        }

        // Determine new status
        Post.Status newStatus;
        if ("APPROVED".equalsIgnoreCase(action.getAction())) {
            newStatus = Post.Status.APPROVED;
        } else if ("DISAPPROVED".equalsIgnoreCase(action.getAction())) {
            newStatus = Post.Status.DISAPPROVED;
        } else {
            throw new CustomException("Invalid action", HttpStatus.BAD_REQUEST);
        }

        post.setStatus(newStatus);
        postRepository.save(post);

        // Save review log
        saveReviewLog(reviewer, "POST", post.getId(), action);

        return mapPostToDto(post);
    }

    @Override
    public List<CommentDto> getCommentsByStatus(Comment.Status status) {
        return commentRepository.findByStatus(status)
                .stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto reviewComment(Long commentId, ModeratorActionDto action) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String reviewerEmail = auth.getName();
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new CustomException("Reviewer not found", HttpStatus.NOT_FOUND));

        // Prevent reviewing own comment
        if (comment.getAuthor().getId().equals(reviewer.getId())) {
            throw new CustomException("You cannot review your own comment", HttpStatus.FORBIDDEN);
        }

        Comment.Status newStatus;
        if ("APPROVED".equalsIgnoreCase(action.getAction())) {
            newStatus = Comment.Status.APPROVED;
        } else if ("DISAPPROVED".equalsIgnoreCase(action.getAction())) {
            newStatus = Comment.Status.DISAPPROVED;
        } else {
            throw new CustomException("Invalid action", HttpStatus.BAD_REQUEST);
        }

        comment.setStatus(newStatus);
        commentRepository.save(comment);

        // Save review log
        saveReviewLog(reviewer, "COMMENT", comment.getId(), action);

        return mapCommentToDto(comment);
    }

    // Helper method to save review log entry
    private void saveReviewLog(User reviewer, String entityType, Long entityId, ModeratorActionDto action) {
        ReviewLog log = new ReviewLog();
        log.setReviewer(reviewer);
        log.setEntityType(ReviewLog.EntityType.valueOf(entityType));
        log.setEntityId(entityId);
        log.setAction(ReviewLog.Action.valueOf(action.getAction().toUpperCase()));
        log.setReviewedAt(LocalDateTime.now());

        reviewLogRepository.save(log);
    }

    private PostDto mapPostToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus(post.getStatus().name());
        dto.setAuthorName(post.getAuthor().getUsername());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }

    private CommentDto mapCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setStatus(comment.getStatus().name());
        dto.setAuthorName(comment.getAuthor().getUsername());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
