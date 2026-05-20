package efub.assignment.community.board.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.dto.request.CreateBoardRequestDto;
import efub.assignment.community.board.dto.request.UpdateBoardRequestDto;
import efub.assignment.community.board.dto.response.BoardResponseDto;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public BoardResponseDto createBoard(CreateBoardRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.memberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Board board = Board.builder()
                .member(member)
                .boardName(requestDto.boardName())
                .description(requestDto.description())
                .notice(requestDto.notice())
                .build();

        Board savedBoard = boardRepository.save(board);
        return BoardResponseDto.from(savedBoard);
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long boardId) {
        Board board = findByBoardId(boardId);
        return BoardResponseDto.from(board);
    }

    public BoardResponseDto updateBoard(Long boardId, Long memberId, UpdateBoardRequestDto requestDto){
        Board board = findByBoardId(boardId);
        authorizeBoardOwner(board, memberId);

        board.updateBoardInfo(
                requestDto.boardName(),
                requestDto.description(),
                requestDto.notice()
        );

        return BoardResponseDto.from(board);
    }

    public void deleteBoard(Long boardId, Long memberId) {
        Board board = findByBoardId(boardId);
        authorizeBoardOwner(board, memberId);

        boardRepository.delete(board);
    }

    public Board findByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    private void authorizeBoardOwner(Board board, Long memberId) {
        if (!board.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.BOARD_MEMBER_MISMATCH);
        }
    }
}