package com.coditas.postly_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminRequestDto {
    private Long id;
    private String status;
    private String username;
    private LocalDateTime requestedAt;
    private String reviewedBy;
}
