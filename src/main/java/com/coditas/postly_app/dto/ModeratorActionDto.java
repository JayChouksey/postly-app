package com.coditas.postly_app.dto;

import lombok.Data;

@Data
public class ModeratorActionDto {
    Long reviewerId;
    String action;
}
