package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class ModeratorUpdateRequestDto {
    Long adminId;
    String action;
}
