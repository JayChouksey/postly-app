package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.ReviewLog;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.PostRepository;
import com.coditas.postly_app.repository.ReviewLogRepository;
import com.coditas.postly_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReviewLogRepository reviewLogRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, ReviewLogRepository reviewLogRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.reviewLogRepository = reviewLogRepository;
    }

    @Override
    public PostResponseDto createPost(PostCreateRequestDto postCreateRequestDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User author = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException("Requester user not found", HttpStatus.NOT_FOUND));

        Post post = new Post();
        post.setTitle(postCreateRequestDto.getTitle());
        post.setContent(postCreateRequestDto.getContent());
        post.setAuthor(author);
        post.setStatus(Post.Status.PENDING);

        Post savedPost = postRepository.save(post);

        return mapPostToDto(savedPost);
    }

    @Override
    public List<PostWithCommentResponseDto> getAllApprovedPosts() {
        return postRepository.findByStatus(Post.Status.APPROVED)
                .stream().map(this::mapPostWithCommentsToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> getPostsByStatus(Post.Status status) {
        return postRepository.findByStatus(status)
                .stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> getPostsByUser(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return postRepository.findByAuthorId(userId)
                .stream().map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDto> getPostsByUserAndStatus(Long userId, String status) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        // Convert the incoming status string into Post.Status enum
        Post.Status postStatus;
        try {
            postStatus = Post.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException("Invalid post status: " + status, HttpStatus.NOT_ACCEPTABLE);
        }

        return postRepository.findByAuthorIdAndStatus(userId, postStatus)
                .stream().map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND));

        return mapPostToDto(post);
    }

    @Override
    public PostResponseDto updatePost(Long postId, PostCreateRequestDto postCreateRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User author = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException("Requester user not found", HttpStatus.NOT_FOUND));

        post.setTitle(postCreateRequestDto.getTitle());
        post.setContent(postCreateRequestDto.getContent());
        post.setStatus(Post.Status.PENDING);

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new CustomException("You cannot update someone else’s post", HttpStatus.FORBIDDEN);
        }

        return mapPostToDto(postRepository.save(post));
    }

    @Override
    public PostResponseDto reviewPost(Long postId, ActionRequestDto action) {
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
        saveReviewLog(reviewer, post.getId(), action);

        return mapPostToDto(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authorEmail = auth.getName();
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new CustomException("You cannot delete someone else’s post", HttpStatus.FORBIDDEN);
        }

        postRepository.delete(post);
    }

    // Helper method to save review log entry
    private void saveReviewLog(User reviewer, Long entityId, ActionRequestDto action) {
        ReviewLog log = new ReviewLog();
        log.setReviewer(reviewer);
        log.setEntityType(ReviewLog.EntityType.valueOf("POST"));
        log.setEntityId(entityId);
        log.setAction(ReviewLog.Action.valueOf(action.getAction().toUpperCase()));
        log.setReviewedAt(LocalDateTime.now());

        reviewLogRepository.save(log);
    }

    private PostWithCommentResponseDto mapPostWithCommentsToDto(Post post) {
        // Map approved comments
        List<CommentResponseDto> approvedComments = post.getComments().stream()
                .filter(c -> c.getStatus() == Comment.Status.APPROVED) // only approved
                .map(c -> {
                    CommentResponseDto cdto = new CommentResponseDto();
                    cdto.setId(c.getId());
                    cdto.setContent(c.getContent());
                    cdto.setStatus(String.valueOf(c.getStatus()));
                    cdto.setAuthorName(c.getAuthor().getUsername());
                    cdto.setPostId(c.getPost().getId());
                    cdto.setCreatedAt(c.getCreatedAt());
                    return cdto;
                })
                .toList();

        return PostWithCommentResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .status(String.valueOf(post.getStatus()))
                .authorName(post.getAuthor().getUsername())
                .createdAt(post.getCreatedAt())
                .approvedComments(approvedComments)
                .build();
    }

    private PostResponseDto mapPostToDto(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .status(post.getStatus().name())
                .authorName(post.getAuthor().getUsername())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
