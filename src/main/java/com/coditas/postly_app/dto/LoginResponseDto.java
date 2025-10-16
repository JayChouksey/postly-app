package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    Long id;
    String username;
    String email;
    String accessToken;
    String refreshToken;
    String role;
}
