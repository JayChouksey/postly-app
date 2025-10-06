package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class AdminUpdateRequestDto {
    Long superAdminId;
    String action;
}
