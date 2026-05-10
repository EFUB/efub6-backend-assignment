package efub.assignment.community.board.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.dto.request.CreateBoardRequest;
import efub.assignment.community.board.dto.request.UpdateBoardRequest;
import efub.assignment.community.board.dto.response.BoardResponse;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createBoard(Long memberId, CreateBoardRequest request) {
        Member writerAccount = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        Board newBoard = request.toEntity(writerAccount);

        boardRepository.save(newBoard);
        return newBoard.getId();
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId) {
        Board board = findByBoardId(boardId);
        return BoardResponse.from(board);
    }

    @Transactional
    public void updateBoardOwner(Long boardId, Long memberId, @Valid UpdateBoardRequest request) {
        Board board = findByBoardId(boardId);

        // 기존 주인
        Member currentOwner = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 새 주인
        Member newOwner = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        authorizeBoardWriter(board, currentOwner);
        board.changeOwner(newOwner);
    }

    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = findByBoardId(boardId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        authorizeBoardWriter(board, member);
        boardRepository.delete(board);
    }

    private Board findByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    private void authorizeBoardWriter(Board board, Member member) {
        if (!board.getWriter().getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.BOARD_ACCOUNT_MISMATCH);
        }
    }

}
