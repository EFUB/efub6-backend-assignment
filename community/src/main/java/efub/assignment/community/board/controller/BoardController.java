package efub.assignment.community.board.controller;

import efub.assignment.community.board.dto.request.CreateBoardRequestDto;
import efub.assignment.community.board.dto.request.UpdateBoardRequestDto;
import efub.assignment.community.board.dto.response.BoardResponseDto;
import efub.assignment.community.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody @Valid CreateBoardRequestDto requestDto) {
        BoardResponseDto responseDto = boardService.createBoard(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable("boardId") Long boardId) {
        BoardResponseDto responseDto = boardService.getBoard(boardId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable("boardId") Long boardId,
                                                        @RequestBody @Valid UpdateBoardRequestDto requestDto) {
        BoardResponseDto responseDto = boardService.updateBoard(boardId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, String>> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}