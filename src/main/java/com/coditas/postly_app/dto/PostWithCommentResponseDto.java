package com.coditas.postly_app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostWithCommentResponseDto {
    private Long id;
    private String title;
    private String content;
    private String status;
    private String authorName;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> approvedComments;
}

