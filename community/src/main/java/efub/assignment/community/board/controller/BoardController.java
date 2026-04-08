package efub.assignment.community.board.controller;

import efub.assignment.community.board.dto.request.BoardCreateRequest;
import efub.assignment.community.board.dto.request.BoardUpdateRequest;
import efub.assignment.community.board.dto.response.BoardResponseDto;
import efub.assignment.community.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Void> createBoard(@RequestBody BoardCreateRequest request) {
        Long id = boardService.createBoard(request);

        return ResponseEntity.created(URI.create("/boards/"+id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable("id")Long boardId) {
        BoardResponseDto response = boardService.getBoard(boardId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoardOwner(@PathVariable("id") Long boardId,
                                                 @RequestHeader("Auth-id") Long memberId,
                                                 @RequestBody BoardUpdateRequest request) {
        boardService.updateBoardOwner(boardId, memberId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long boardId,
                                            @RequestHeader("Auth-id") Long memberId) {
        boardService.deleteBoard(boardId,memberId);

        return ResponseEntity.noContent().build();
    }
}
