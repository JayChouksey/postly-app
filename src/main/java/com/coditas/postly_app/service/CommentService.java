package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.CommentRequestDto;
import com.coditas.postly_app.dto.CommentUpdateRequestDto;
import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentDto addComment(CommentRequestDto requestDto);
    List<CommentDto> getCommentsByPost(Long postId);
    List<CommentDto> getCommentsByStatus(Comment.Status status);
    CommentDto updateComment(Long commentId, CommentUpdateRequestDto requestDto);
    CommentDto reviewComment(Long commentId, ModeratorActionDto action);
    void deleteComment(Long commentId);
}
