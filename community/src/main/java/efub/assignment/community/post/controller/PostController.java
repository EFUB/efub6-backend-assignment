package efub.assignment.community.post.controller;

import efub.assignment.community.post.dto.request.PostUpdateRequest;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시물 상세 조회
    @Transactional
    @GetMapping
    public ResponseEntity<PostResponse> getPost(@PathVariable("postId") Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    // 게시물 수정
    @Transactional
    @PatchMapping
    public ResponseEntity<PostResponse> updatePostContent(@PathVariable("postId") Long postId,
                                                          @RequestHeader("Auth-Id") Long memberId,
                                                          @Valid @RequestBody PostUpdateRequest request) {
        PostResponse response = postService.updatePostContent(postId, memberId, request);
        return ResponseEntity.ok(response);
    }

    // 게시물 삭제
    @Transactional
    @DeleteMapping
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId,
                                           @RequestHeader("Auth-Id") Long memberId) {
        postService.deletePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}
