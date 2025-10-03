package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class CommentRequestDto {
    private String content;
    private Long postId;
}