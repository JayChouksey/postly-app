package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.CommentRequestDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(CommentRequestDto requestDto);
    CommentDto updateComment(Long commentId, CommentRequestDto requestDto);
//    void deleteComment(Long commentId, Long userId);
    List<CommentDto> getCommentsByPost(Long postId);
    CommentDto approveComment(Long commentId, Long reviewerId, boolean approved);
}
