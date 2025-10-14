package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;
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
    public String createPost(PostRequestDto postRequestDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User author = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException("Requester user not found", HttpStatus.NOT_FOUND));

        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setAuthor(author);
        post.setStatus(Post.Status.PENDING);

        postRepository.save(post);

        return "Post create successfully!";
    }

    @Override
    public List<PostDto> getAllApprovedPosts() {
        return postRepository.findByStatus(Post.Status.APPROVED)
                .stream().map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByStatus(Post.Status status) {
        return postRepository.findByStatus(status)
                .stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByUser(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return postRepository.findByAuthorId(userId)
                .stream().map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByUserAndStatus(Long userId, String status) {

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
                .stream().map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND));

        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(Long postId, PostRequestDto postRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post not found", HttpStatus.NOT_FOUND));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User author = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException("Requester user not found", HttpStatus.NOT_FOUND));

        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setStatus(Post.Status.PENDING);

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new CustomException("You cannot update someone else’s post", HttpStatus.FORBIDDEN);
        }

        return mapToDto(postRepository.save(post));
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
    private void saveReviewLog(User reviewer, Long entityId, ModeratorActionDto action) {
        ReviewLog log = new ReviewLog();
        log.setReviewer(reviewer);
        log.setEntityType(ReviewLog.EntityType.valueOf("POST"));
        log.setEntityId(entityId);
        log.setAction(ReviewLog.Action.valueOf(action.getAction().toUpperCase()));
        log.setReviewedAt(LocalDateTime.now());

        reviewLogRepository.save(log);
    }

    private PostDto mapToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus(String.valueOf(post.getStatus()));
        dto.setAuthorName(post.getAuthor().getUsername());
        dto.setCreatedAt(post.getCreatedAt());

        // Map approved comments
        List<CommentDto> approvedComments = post.getComments().stream()
                .filter(c -> c.getStatus() == Comment.Status.APPROVED) // only approved
                .map(c -> {
                    CommentDto cdto = new CommentDto();
                    cdto.setId(c.getId());
                    cdto.setContent(c.getContent());
                    cdto.setStatus(String.valueOf(c.getStatus()));
                    cdto.setAuthorName(c.getAuthor().getUsername());
                    cdto.setPostId(c.getPost().getId());
                    cdto.setCreatedAt(c.getCreatedAt());
                    return cdto;
                })
                .toList();

        dto.setApprovedComments(approvedComments);

        return dto;
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
}
