package com.coditas.postly_app.repository;

import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByPostId(Long postId);
    List<Comment> findByAuthor(User author);
    List<Comment> findByStatus(String status);
}

