package com.coditas.postly_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModeratorRequestDto {
    private Long id;
    private String status;
    private String username;
    private LocalDateTime requestedAt;
    private String reviewedBy; // admin username
}
