package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.CommentRequestDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.CommentRepository;
import com.coditas.postly_app.repository.PostRepository;
import com.coditas.postly_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentDto addComment(CommentRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(requestDto.getContent());
        comment.setAuthor(user);
        comment.setPost(post);
        comment.setStatus(Comment.Status.PENDING);

        return mapToDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(requestDto.getContent());
        return mapToDto(commentRepository.save(comment));
    }

//    @Override
//    public void deleteComment(Long commentId, Long userId) {
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
//        if (!comment.getAuthor().getId().equals(userId)) {
//            throw new RuntimeException("You cannot delete someone else’s comment");
//        }
//        commentRepository.delete(comment);
//    }

    @Override
    public List<CommentDto> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto approveComment(Long commentId, Long reviewerId, boolean approved) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus(approved ? Comment.Status.APPROVED : Comment.Status.DISAPPROVED);
        return mapToDto(commentRepository.save(comment));
    }

    private CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setStatus(String.valueOf(comment.getStatus()));
        dto.setAuthorName(comment.getAuthor().getUsername());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}

