package com.coditas.postly_app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateResponseDto {
    private Long id;
    private String username;
    private String email;
    private String role;
}