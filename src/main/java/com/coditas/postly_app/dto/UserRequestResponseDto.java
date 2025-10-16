package com.coditas.postly_app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserRequestResponseDto {
    private Long id;
    private String status;
    private String username;
    private LocalDateTime requestedAt;
}
