package com.coditas.postly_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentUpdateRequestDto {
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 500, message = "Content must be between 1 and 500 characters")
    private String content;
}
