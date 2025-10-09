package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;

import java.util.List;

public interface PostService {
    String createPost(PostRequestDto postRequestDto);
    PostDto updatePost(Long postId, PostRequestDto postRequestDto);
    void deletePost(Long postId);
    List<PostDto> getAllApprovedPosts();
    List<PostDto> getPostsByUser(Long userId);
    List<PostDto> getPostsByUserAndStatus(Long userId, String status);
    PostDto approvePost(Long postId, Long reviewerId, boolean approved);
    PostDto getPostById(Long id);
}