package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentResponseDto;
import com.coditas.postly_app.dto.CommentCreateRequestDto;
import com.coditas.postly_app.dto.CommentUpdateRequestDto;
import com.coditas.postly_app.dto.ActionRequestDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.ReviewLog;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.CommentRepository;
import com.coditas.postly_app.repository.PostRepository;
import com.coditas.postly_app.repository.ReviewLogRepository;
import com.coditas.postly_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReviewLogRepository reviewLogRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, ReviewLogRepository reviewLogRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.reviewLogRepository = reviewLogRepository;
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentCreateRequestDto requestDto) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException("Requester user not found", HttpStatus.NOT_FOUND));

        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND));

        if(post.getStatus() != Post.Status.APPROVED){
            throw new CustomException("Comment can only done on Approved Posts", HttpStatus.FORBIDDEN);
        }

        Comment comment = new Comment();
        comment.setContent(requestDto.getContent());
        comment.setAuthor(user);
        comment.setPost(post);
        comment.setStatus(Comment.Status.PENDING);

        return mapToDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponseDto> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentResponseDto> getCommentsByStatus(Comment.Status status) {
        return commentRepository.findByStatus(status)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentUpdateRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authorEmail = auth.getName();
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new CustomException("You cannot update someone else’s comment", HttpStatus.FORBIDDEN);
        }

        comment.setContent(requestDto.getContent());
        comment.setStatus(Comment.Status.PENDING);
        return mapToDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponseDto reviewComment(Long commentId, ActionRequestDto action) {
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
        saveReviewLog(reviewer, comment.getId(), action);

        return mapToDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authorEmail = auth.getName();
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new CustomException("You cannot delete someone else’s comment", HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
    }

    // Helper method to save review log entry
    private void saveReviewLog(User reviewer, Long entityId, ActionRequestDto action) {
        ReviewLog log = new ReviewLog();
        log.setReviewer(reviewer);
        log.setEntityType(ReviewLog.EntityType.valueOf("COMMENT"));
        log.setEntityId(entityId);
        log.setAction(ReviewLog.Action.valueOf(action.getAction().toUpperCase()));
        log.setReviewedAt(LocalDateTime.now());

        reviewLogRepository.save(log);
    }

    private CommentResponseDto mapToDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setStatus(String.valueOf(comment.getStatus()));
        dto.setAuthorName(comment.getAuthor().getUsername());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}

