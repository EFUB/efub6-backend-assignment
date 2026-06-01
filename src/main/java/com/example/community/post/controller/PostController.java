package com.example.community.post.controller;

import com.example.community.post.dto.request.CreatePostRequest;
import com.example.community.post.dto.request.UpdatePostRequest;
import com.example.community.post.dto.response.PostListResponse;
import com.example.community.post.dto.response.PostResponse;
import com.example.community.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 글 생성
    @PostMapping("/boards/{boardId}/posts")
    public ResponseEntity<PostResponse> createPost(@PathVariable Long boardId,
                                                   @RequestHeader("Auth-Id") Long memberId,
                                                   @Valid @RequestBody CreatePostRequest request) {
        PostResponse response = postService.createPost(boardId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 글 목록조회
    @GetMapping("/boards/{boardId}/posts")
    public ResponseEntity<PostListResponse> getAllPosts(@PathVariable Long boardId) {
        PostListResponse response = postService.getAllPosts(boardId);
        return ResponseEntity.ok(response);
    }

    // 글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    // 글 수정
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePostContent(@PathVariable Long postId,
                                                  @RequestHeader("Auth-Id") Long memberId,
                                                  @Valid @RequestBody UpdatePostRequest request) {
        postService.updatePostContent(postId, memberId, request);
        return ResponseEntity.noContent().build();
    }

    // 글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @RequestHeader("Auth-Id") Long memberId) {
        postService.deletePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }

    // 글 좋아요 생성
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> createPostLike(@PathVariable Long postId,
                                               @RequestHeader("Auth-Id") Long memberId) {
        postService.createPostLike(postId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 글 좋아요 삭제
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> deletePostLike(@PathVariable Long postId,
                                               @RequestHeader("Auth-Id") Long memberId) {
        postService.deletePostLike(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}
