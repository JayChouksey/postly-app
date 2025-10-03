package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class PostRequestDto {
    private Long userId;
    private String title;
    private String content;
}
