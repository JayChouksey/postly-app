package com.coditas.postly_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @PrePersist
    protected void onCreate(){
        requestedAt = LocalDateTime.now();
        status = Status.PENDING;
    }
}
