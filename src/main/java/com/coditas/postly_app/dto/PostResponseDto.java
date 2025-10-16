package com.coditas.postly_app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String status;
    private String authorName;
    private LocalDateTime createdAt;
}
