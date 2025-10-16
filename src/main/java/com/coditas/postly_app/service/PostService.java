package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ActionRequestDto;
import com.coditas.postly_app.dto.PostResponseDto;
import com.coditas.postly_app.dto.PostWithCommentResponseDto;
import com.coditas.postly_app.dto.PostCreateRequestDto;
import com.coditas.postly_app.entity.Post;

import java.util.List;

public interface PostService {
    PostResponseDto createPost(PostCreateRequestDto postCreateRequestDto);
    List<PostWithCommentResponseDto> getAllApprovedPosts();
    List<PostResponseDto> getPostsByStatus(Post.Status status);
    List<PostResponseDto> getPostsByUser(Long userId);
    List<PostResponseDto> getPostsByUserAndStatus(Long userId, String status);
    PostResponseDto getPostById(Long id);
    PostResponseDto updatePost(Long postId, PostCreateRequestDto postCreateRequestDto);
    PostResponseDto reviewPost(Long postId, ActionRequestDto action);
    void deletePost(Long postId);
}