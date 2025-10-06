package com.coditas.postly_app.repository;

import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);
    List<Post> findByAuthorId(Long userId);
    List<Post> findByAuthorIdAndStatus(Long userId, Post.Status status);
    List<Post> findByStatus(Post.Status status);
}
