package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;   // Role name only
    private boolean isModerator;
}