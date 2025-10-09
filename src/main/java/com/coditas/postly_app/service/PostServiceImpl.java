package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.CommentDto;
import com.coditas.postly_app.dto.PostDto;
import com.coditas.postly_app.dto.PostRequestDto;
import com.coditas.postly_app.entity.Comment;
import com.coditas.postly_app.entity.Post;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.PostRepository;
import com.coditas.postly_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String createPost(PostRequestDto postRequestDto) {
        User author = userRepository.findById(postRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setAuthor(author);
        post.setStatus(Post.Status.PENDING);

        postRepository.save(post);

        return "Post create successfully!";
    }

    @Override
    public PostDto updatePost(Long postId, PostRequestDto postRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setStatus(Post.Status.PENDING);

        if (!post.getAuthor().getId().equals(postRequestDto.getUserId())) {
            throw new CustomException("You cannot update someone else’s post", HttpStatus.FORBIDDEN);
        }

        return mapToDto(postRepository.save(post));
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String authorEmail = auth.getName();
//        User author = userRepository.findByEmail(authorEmail)
//                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
//
//        if (!post.getAuthor().getId().equals(author.getId())) {
//            throw new CustomException("You cannot delete someone else’s post", HttpStatus.FORBIDDEN);
//        }

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
    public List<PostDto> getPostsByUserAndStatus(Long userId, String status) {

        // Convert the incoming status string (e.g., "approved") into Post.Status enum
        Post.Status postStatus;
        try {
            postStatus = Post.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid post status: " + status);
        }

        return postRepository.findByAuthorIdAndStatus(userId, postStatus)
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

        // Map approved comments
        List<CommentDto> approvedComments = post.getComments().stream()
                .filter(c -> c.getStatus() == Comment.Status.APPROVED) // only approved
                .map(c -> {
                    CommentDto cdto = new CommentDto();
                    cdto.setId(c.getId());
                    cdto.setContent(c.getContent());
                    cdto.setStatus(String.valueOf(c.getStatus()));
                    cdto.setAuthorName(c.getAuthor().getUsername());
                    cdto.setPostId(c.getPost().getId());
                    cdto.setCreatedAt(c.getCreatedAt());
                    return cdto;
                })
                .toList();

        dto.setApprovedComments(approvedComments);

        return dto;
    }
}
