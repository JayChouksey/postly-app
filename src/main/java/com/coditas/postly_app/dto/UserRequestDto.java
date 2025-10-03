package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class UserRequestDto {
    private String username;
    private String email;
    private String password;
}
