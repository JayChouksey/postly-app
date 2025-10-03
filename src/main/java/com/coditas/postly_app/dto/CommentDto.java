package com.coditas.postly_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private String status;
    private String authorName;
    private Long postId;
    private LocalDateTime createdAt;
}
