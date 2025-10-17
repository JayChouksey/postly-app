package com.coditas.postly_app.repository;

import com.coditas.postly_app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByStatus(Comment.Status status);
}

