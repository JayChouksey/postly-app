package com.coditas.postly_app.repository;
import com.coditas.postly_app.entity.AdminRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
    List<AdminRequest> findAllByStatus(AdminRequest.Status status);
}

