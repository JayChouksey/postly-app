package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ReviewLogDto;
import com.coditas.postly_app.entity.ReviewLog;
import com.coditas.postly_app.repository.ReviewLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewLogServiceImpl {
//public class ReviewLogServiceImpl implements ReviewLogService {

//    private final ReviewLogRepository reviewLogRepository;
//
//    @Autowired
//    public ReviewLogServiceImpl(ReviewLogRepository reviewLogRepository) {
//        this.reviewLogRepository = reviewLogRepository;
//    }

//    @Override
//    public void logReview(String reviewerName, String entityType, Long entityId, String action) {
//        ReviewLog log = new ReviewLog();
//        log.setReviewerName(reviewerName);
//        log.setEntityType(ReviewLog.EntityType.valueOf(entityType));
//        log.setEntityId(entityId);
//        log.setAction(ReviewLog.Action.valueOf(action));
//        reviewLogRepository.save(log);
//    }

//    @Override
//    public List<ReviewLogDto> getAllLogs() {
//        return reviewLogRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ReviewLogDto> getLogsByReviewer(Long reviewerId) {
//        return reviewLogRepository.findByReviewerId(reviewerId).stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    private ReviewLogDto mapToDto(ReviewLog log) {
//        ReviewLogDto dto = new ReviewLogDto();
//        dto.setId(log.getId());
//        dto.setReviewerName(log.getReviewerName());
//        dto.setEntityType(String.valueOf(log.getEntityType()));
//        dto.setEntityId(log.getEntityId());
//        dto.setAction(String.valueOf(log.getAction()));
//        dto.setReviewedAt(log.getReviewedAt());
//        return dto;
//    }
}

