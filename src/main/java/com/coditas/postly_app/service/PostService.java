package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ModeratorActionDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;
import com.coditas.postly_app.entity.Post;

import java.util.List;

public interface PostService {
    String createPost(PostRequestDto postRequestDto);
    List<PostDto> getAllApprovedPosts();
    List<PostDto> getPostsByStatus(Post.Status status);
    List<PostDto> getPostsByUser(Long userId);
    List<PostDto> getPostsByUserAndStatus(Long userId, String status);
    PostDto getPostById(Long id);
    PostDto updatePost(Long postId, PostRequestDto postRequestDto);
    PostDto reviewPost(Long postId, ModeratorActionDto action);
    void deletePost(Long postId);
}