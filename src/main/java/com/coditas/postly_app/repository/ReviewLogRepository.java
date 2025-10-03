package com.coditas.postly_app.repository;

import com.coditas.postly_app.entity.ReviewLog;
import com.coditas.postly_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {
    List<ReviewLog> findByReviewer(User reviewer);
    List<ReviewLog> findByReviewerId(Long reviewerId);
    List<ReviewLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}

