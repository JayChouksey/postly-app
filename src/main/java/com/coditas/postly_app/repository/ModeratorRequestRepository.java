package com.coditas.postly_app.repository;

import com.coditas.postly_app.entity.ModeratorRequest;
import com.coditas.postly_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModeratorRequestRepository extends JpaRepository<ModeratorRequest, Long> {
    List<ModeratorRequest> findByUser(User user);
    List<ModeratorRequest> findAllByStatus(ModeratorRequest.Status status);
    boolean existsByUserIdAndStatus(Long userId, ModeratorRequest.Status status);
}
