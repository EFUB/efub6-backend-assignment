package efub.assignment.community.board.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.dto.request.BoardCreateRequest;
import efub.assignment.community.board.dto.request.BoardUpdateRequest;
import efub.assignment.community.board.dto.response.BoardResponse;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MembersService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MembersService membersService;

    @Transactional
    public Long createBoard(BoardCreateRequest request) {
        Member writerAccount = membersService.findByMemberId(request.getMemberId());

        Board newBoard = request.toEntity(writerAccount);
        boardRepository.save(newBoard);
        return newBoard.getId();
    }

    @Transactional
    public BoardResponse getBoard(Long boardId) {
        Board board = findByBoardId(boardId);
        return BoardResponse.from(board);
    }

    @Transactional
    public void updateBoardOwner(Long boardId, Long memberId, @Valid BoardUpdateRequest request) {
        Board board = findByBoardId(boardId);
        Member currentOwner = membersService.findByMemberId(memberId); // 기존 주인
        Member newOwner = membersService.findByNickname(request.nickname()); // 새 주인

        authorizeBoardWriter(board, currentOwner);
        board.changeOwner(newOwner);
    }

    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = findByBoardId(boardId);
        Member member = membersService.findByMemberId(memberId);

        authorizeBoardWriter(board, member);
        boardRepository.delete(board);
    }

    private Board findByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(()->new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    private void authorizeBoardWriter(Board board, Member member) {
        if(!board.getWriter().getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.BOARD_ACCOUNT_MISMATCH);
        }
    }

}
