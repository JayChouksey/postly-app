package com.coditas.postly_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
}
