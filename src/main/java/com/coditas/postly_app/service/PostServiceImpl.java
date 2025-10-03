package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.PostRepository;
import com.coditas.postly_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PostDto createPost(PostRequestDto postRequestDto) {
        User author = userRepository.findById(postRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setAuthor(author);
        post.setStatus(Post.Status.PENDING);

        Post saved = postRepository.save(post);

        return mapToDto(saved);
    }

    @Override
    public PostDto updatePost(Long postId, PostRequestDto postRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());

        return mapToDto(postRepository.save(post));
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You cannot delete someone else's post");
        }
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getAllApprovedPosts() {
        return postRepository.findByStatus(Post.Status.APPROVED)
                .stream().map(this::mapToDto)
                .collect(Collectors.toList());
    }



    @Override
    public List<PostDto> getPostsByUser(Long userId) {
        return postRepository.findByAuthorId(userId)
                .stream().map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto approvePost(Long postId, Long reviewerId, boolean approved) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setStatus(approved ? Post.Status.APPROVED : Post.Status.DISAPPROVED);

        return mapToDto(postRepository.save(post));
    }

    @Override
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return mapToDto(post);
    }

    private PostDto mapToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus(String.valueOf(post.getStatus()));
        dto.setAuthorName(post.getAuthor().getUsername());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}
