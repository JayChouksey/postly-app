package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class LoginDto {
    Long id;
    String username;
    String email;
    String token;
    String role;
    Boolean isModerator;

}
