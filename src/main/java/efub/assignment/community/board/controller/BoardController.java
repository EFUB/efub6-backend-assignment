package efub.assignment.community.board.controller;

import efub.assignment.community.board.dto.request.CreateBoardRequest;
import efub.assignment.community.board.dto.request.UpdateBoardRequest;
import efub.assignment.community.board.dto.response.BoardResponse;
import efub.assignment.community.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시판 생성
    @PostMapping("/members/{memberId}/boards")
    public ResponseEntity<Void> createBoard(@PathVariable("memberId") Long memberId,
                                            @Valid @RequestBody CreateBoardRequest request) {
        Long boardId = boardService.createBoard(memberId, request);
        return ResponseEntity.created(URI.create("/boards/"+boardId)).build();
    }

    // 게시판 조회
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable("boardId") Long boardId) {
        BoardResponse response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    // 게시판 주인 수정
    @PatchMapping("/boards/{boardId}")
    public ResponseEntity<Void> updateBoardOwner(@PathVariable("boardId") Long boardId,
                                                  @RequestHeader("Auth-Id") Long memberId,
                                                  @Valid @RequestBody UpdateBoardRequest request) {
        boardService.updateBoardOwner(boardId, memberId, request);
        return ResponseEntity.noContent().build();
    }

    // 게시판 삭제
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("boardId") Long boardId,
                                           @RequestHeader("Auth-Id") Long memberId) {
        boardService.deleteBoard(boardId, memberId);
        return ResponseEntity.noContent().build();
    }
}
