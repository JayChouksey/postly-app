package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;

import java.util.List;

public interface PostService {
    PostDto createPost(PostRequestDto postRequestDto);
    PostDto updatePost(Long postId, PostRequestDto postRequestDto);
    void deletePost(Long postId, Long userId);
    List<PostDto> getAllApprovedPosts();
    List<PostDto> getPostsByUser(Long userId);
    PostDto approvePost(Long postId, Long reviewerId, boolean approved);
    PostDto getPostById(Long id);
}