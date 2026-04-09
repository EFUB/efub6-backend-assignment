package efub.assignment.community.post.controller;

import efub.assignment.community.post.dto.request.PostCreateRequest;
import efub.assignment.community.post.dto.response.PostListResponse;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/{boardId}/posts")
@RequiredArgsConstructor
public class BoardPostController {

    private final PostService postService;

    // 게시물 생성
    @Transactional
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@PathVariable("boardId") Long boardId,
                                                   @RequestHeader("Auth-Id") Long memberId,
                                                   @Valid @RequestBody PostCreateRequest request) {
        PostResponse response = postService.createPost(boardId, memberId, request);
        return ResponseEntity.ok(response);
    }

    // 게시물 목록 조회
    @Transactional
    @GetMapping
    public ResponseEntity<PostListResponse> getAllPosts() {
        PostListResponse response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }
}
