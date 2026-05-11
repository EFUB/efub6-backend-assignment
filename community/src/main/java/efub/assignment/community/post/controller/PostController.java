package efub.assignment.community.post.controller;

import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.request.PostUpdateRequestDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostCreateRequestDto request) {
        Long id = postService.createPost(request);

        return ResponseEntity.created(URI.create("/posts/"+id)).build();
    }

    @GetMapping
    public ResponseEntity<PostListResponseDto> getAllPosts() {
        PostListResponseDto response = postService.getAllPosts();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost (@PathVariable("id") Long postId) {
        PostResponseDto response = postService.getPost(postId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePostContent (@PathVariable("id") Long postId,
                                                   @RequestHeader("Auth-id") Long memberId,
                                                   @Valid @RequestBody PostUpdateRequestDto request) {
        postService.updatePostContent(postId, memberId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost (@PathVariable("id") Long postId,
                                            @RequestHeader("Auth-id") Long memberId) {
        postService.deletePost(postId, memberId);

        return ResponseEntity.noContent().build();
    }

}
