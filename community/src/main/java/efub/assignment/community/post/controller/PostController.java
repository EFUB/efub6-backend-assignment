package efub.assignment.community.post.controller;

import efub.assignment.community.post.dto.request.CreatePostRequestDto;
import efub.assignment.community.post.dto.request.UpdatePostRequestDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/boards/{boardId}/posts")
    public ResponseEntity<PostResponseDto> createPost(@PathVariable("boardId") Long boardId,
                                                      @RequestHeader("Auth-Id") Long memberId,
                                                      @RequestBody @Valid CreatePostRequestDto requestDto) {
        PostResponseDto responseDto = postService.createPost(boardId, memberId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/boards/{boardId}/posts")
    public ResponseEntity<PostListResponseDto> getPostsByBoard(@PathVariable("boardId") Long boardId) {
        PostListResponseDto responseDto = postService.getPostsByBoard(boardId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable("postId") Long postId) {
        PostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable("postId") Long postId,
                                                      @RequestHeader("Auth-Id") Long memberId,
                                                      @RequestBody @Valid UpdatePostRequestDto requestDto) {
        PostResponseDto responseDto = postService.updatePost(postId, memberId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable("postId") Long postId,
                                                          @RequestHeader("Auth-Id") Long memberId) {
        postService.deletePost(postId, memberId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<String> likePost(
            @PathVariable("postId") Long postId,
            @RequestHeader("Auth-Id") Long memberId
    ) {
        postService.likePost(postId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body("좋아요를 눌렀습니다.");
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable("postId") Long postId,
            @RequestHeader("Auth-Id") Long memberId
    ) {
        postService.unlikePost(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}