package com.example.community.board.controller;

import com.example.community.board.dto.request.CreateBoardRequest;
import com.example.community.board.dto.request.UpdateBoardOwnerRequest;
import com.example.community.board.dto.response.BoardListResponse;
import com.example.community.board.dto.response.BoardResponse;
import com.example.community.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시판 생성
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@RequestHeader("Auth-Id") Long memberId,
                                                     @Valid @RequestBody CreateBoardRequest request) {
        BoardResponse response = boardService.createBoard(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 게시판 목록 조회
    @GetMapping
    public ResponseEntity<BoardListResponse> getAllBoards() {
        BoardListResponse response = boardService.getAllBoards();
        return ResponseEntity.ok(response);
    }

    // 게시판 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long boardId) {
        BoardResponse response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    // 게시판 주인 수정
    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> updateBoardOwner(@PathVariable Long boardId,
                                                 @RequestHeader("Auth-Id") Long memberId,
                                                 @Valid @RequestBody UpdateBoardOwnerRequest request) {
        boardService.updateBoardOwner(boardId, memberId, request);
        return ResponseEntity.noContent().build();
    }

    // 게시판 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId,
                                            @RequestHeader("Auth-Id") Long memberId) {
        boardService.deleteBoard(boardId, memberId);
        return ResponseEntity.noContent().build();
    }
}
