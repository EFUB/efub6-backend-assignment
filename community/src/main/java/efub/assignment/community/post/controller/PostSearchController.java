package efub.assignment.community.post.controller;

import efub.assignment.community.post.dto.response.PostSearchResponseDto;
import efub.assignment.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts/search")
@RequiredArgsConstructor
public class PostSearchController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostSearchResponseDto>> searchPost(
            @RequestParam(value="board", required = false) String boardName,
            @RequestParam(value="writer", required = false) String writerName,
            @RequestParam(value="keyword", required = false) String keyword
    ) {
        return ResponseEntity.ok(postService.searchPost(boardName, writerName, keyword));
    }
}
