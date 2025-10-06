package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.repository.CommentRepository;
import com.coditas.postly_app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModeratorServiceImpl implements ModeratorService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

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
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if ("APPROVE".equalsIgnoreCase(action.getAction())) {
            post.setStatus(Post.Status.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(action.getAction())) {
            post.setStatus(Post.Status.DISAPPROVED);
        } else {
            throw new RuntimeException("Invalid action");
        }

        return mapPostToDto(postRepository.save(post));
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
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if ("APPROVE".equalsIgnoreCase(action.getAction())) {
            comment.setStatus(Comment.Status.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(action.getAction())) {
            comment.setStatus(Comment.Status.DISAPPROVED);
        } else {
            throw new RuntimeException("Invalid action");
        }

        return mapCommentToDto(commentRepository.save(comment));
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

