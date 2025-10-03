package com.coditas.postly_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewLogDto {
    private Long id;
    private String reviewerName;
    private String entityType;  // POST or COMMENT
    private Long entityId;
    private String action;      // APPROVED / DISAPPROVED
    private LocalDateTime reviewedAt;
}
