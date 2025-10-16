package com.coditas.postly_app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateRequestResponseDto {
    Long requestId;
    String username;
    String email;
    String requestedRole;
    String status;
}

