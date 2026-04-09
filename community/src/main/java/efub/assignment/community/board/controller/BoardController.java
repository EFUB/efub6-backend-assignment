package efub.assignment.community.board.controller;

import efub.assignment.community.board.dto.request.BoardCreateRequest;
import efub.assignment.community.board.dto.request.BoardOwnerUpdateRequest;
import efub.assignment.community.board.dto.response.BoardResponse;
import efub.assignment.community.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시판 생성
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardCreateRequest request) {
        BoardResponse response = boardService.createBoard(request);
        return ResponseEntity.ok(response);
    }

    // 게시판 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable("id") Long boardId) {
        BoardResponse response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    // 게시판 주인 수정
    @PatchMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoardOwner(@PathVariable("id") Long boardId,
                                                          @RequestHeader("Auth-Id") Long memberId,
                                                          @RequestBody BoardOwnerUpdateRequest request) {
        BoardResponse response = boardService.updateBoardOwner(boardId, memberId, request);
        return ResponseEntity.ok(response);
    }

    // 게시판 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long boardId,
                                            @RequestHeader("Auth-Id") Long memberId) {
        boardService.deleteBoard(boardId, memberId);
        return ResponseEntity.noContent().build();
    }
}
