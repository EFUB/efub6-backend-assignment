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

    // 게시글 생성
    @PostMapping("/boards/{boardId}/posts")
    public ResponseEntity<Void> createPost(
            @PathVariable("boardId") Long boardId,
            @RequestHeader("Auth-Id") Long memberId,
            @Valid @RequestBody PostCreateRequest request) {
        Long postId = postService.createPost(boardId, memberId, request);
        return ResponseEntity.created(URI.create("/boards/"+boardId+"/posts/"+postId)).build();
    }

    // 게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<PostListResponse> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 게시글 단건 내용 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("postId") Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // 게시글 수정
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePostContent(@PathVariable("postId") Long postId,
                                                  @RequestHeader("Auth-Id") Long memberId,
                                                  @RequestBody PostUpdateRequest request) {
        postService.updatePostContent(postId, request, memberId);
        return ResponseEntity.noContent().build();
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId,
                                           @RequestHeader("Auth-Id") Long memberId) {
        postService.deletePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}
