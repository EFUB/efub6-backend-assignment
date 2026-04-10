package efub.assignment.community.board.controller;

import efub.assignment.community.board.dto.request.BoardCreateRequest;
import efub.assignment.community.board.dto.request.BoardUpdateRequest;
import efub.assignment.community.board.dto.response.BoardResponse;
import efub.assignment.community.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시판 생성
    @PostMapping
    public ResponseEntity<Void> createBoard(@Valid @RequestBody BoardCreateRequest request) {
        Long id = boardService.createBoard(request);
        return ResponseEntity.created(URI.create("/boards/"+id)).build();
    }

    // 게시판 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable("id") Long id) {
        BoardResponse response = boardService.getBoard(id);
        return ResponseEntity.ok(response);
    }

    // 게시판 주인 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoardOwner(@PathVariable("id") Long boardId,
                                                  @RequestHeader("Auth-Id") Long memberId,
                                                  @Valid @RequestBody BoardUpdateRequest request) {
        boardService.updateBoardOwner(boardId, memberId, request);
        return ResponseEntity.noContent().build();
    }

    // 게시판 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long boardId,
                                           @RequestHeader("Auth-Id") Long memberId) {
        boardService.deleteBoard(boardId, memberId);
        return ResponseEntity.noContent().build();
    }
}
