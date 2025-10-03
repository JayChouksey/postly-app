package com.coditas.postly_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String status;
    private String authorName;
    private LocalDateTime createdAt;
}

