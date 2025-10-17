package com.coditas.postly_app.repository;

import com.coditas.postly_app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long userId);
    List<Post> findByAuthorIdAndStatus(Long userId, Post.Status status);
    List<Post> findByStatus(Post.Status status);
}
