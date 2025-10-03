package com.coditas.postly_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Long entityId;  // Post or Comment ID

    @Enumerated(EnumType.STRING)
    private Action action;

    private LocalDateTime reviewedAt;

    public enum EntityType {
        POST, COMMENT
    }

    public enum Action {
        APPROVED, DISAPPROVED
    }

    @PrePersist
    protected void onCreate(){
        reviewedAt = LocalDateTime.now();
    }
}
