package efub.assignment.community.post.controller;

import efub.assignment.community.post.dto.request.PostCreateRequest;
import efub.assignment.community.post.dto.request.PostUpdateRequest;
import efub.assignment.community.post.dto.response.PostListResponse;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping("/boards/{boardId}/posts")
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long boardId,
            @RequestHeader("Auth-id") Long memberId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostResponse response = postService.createPost(boardId, memberId, request);
        return ResponseEntity
                .created(URI.create("/posts/" + response.postId()))
                .body(response);
    }

    // 게시글 목록 조회
    @GetMapping("/boards/{boardId}/posts")
    public ResponseEntity<PostListResponse> getAllPosts(@PathVariable Long boardId) {
        PostListResponse response = postService.getAllPosts(boardId);
        return ResponseEntity.ok(response);
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    // 게시글 내용 수정
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestHeader("Auth-id") Long memberId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        PostResponse response = postService.updatePost(postId, memberId, request);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestHeader("Auth-id") Long memberId
    ) {
        postService.deletePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}