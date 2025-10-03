package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ReviewLogDto;

import java.util.List;

public interface ReviewLogService {
    void logReview(String reviewerName, String entityType, Long entityId, String action);
    List<ReviewLogDto> getAllLogs();
    List<ReviewLogDto> getLogsByReviewer(Long reviewerId);
}

