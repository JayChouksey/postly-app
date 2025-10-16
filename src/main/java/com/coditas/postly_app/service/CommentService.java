package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentResponseDto;
import com.coditas.postly_app.dto.CommentCreateRequestDto;
import com.coditas.postly_app.dto.CommentUpdateRequestDto;
import com.coditas.postly_app.dto.ActionRequestDto;
import com.coditas.postly_app.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(CommentCreateRequestDto requestDto);
    List<CommentResponseDto> getCommentsByPost(Long postId);
    List<CommentResponseDto> getCommentsByStatus(Comment.Status status);
    CommentResponseDto updateComment(Long commentId, CommentUpdateRequestDto requestDto);
    CommentResponseDto reviewComment(Long commentId, ActionRequestDto action);
    void deleteComment(Long commentId);
}
