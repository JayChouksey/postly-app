package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class UserDtoById {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean hasRequestedModerator;
}