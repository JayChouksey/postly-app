package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;

import java.util.List;

public interface ModeratorService {

    List<PostDto> getPostsByStatus(Post.Status status);

    PostDto reviewPost(Long postId, ModeratorActionDto action);

    List<CommentDto> getCommentsByStatus(Comment.Status status);

    CommentDto reviewComment(Long commentId, ModeratorActionDto action);
}

