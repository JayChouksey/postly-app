package com.coditas.postly_app.repository;
import com.coditas.postly_app.entity.AdminRequest;
import com.coditas.postly_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
    List<AdminRequest> findByUser(User user);
    List<AdminRequest> findByStatus(String status);
}

