package efub.assignment.community.board.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.dto.request.BoardCreateRequest;
import efub.assignment.community.board.dto.request.BoardOwnerUpdateRequest;
import efub.assignment.community.board.dto.response.BoardResponse;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;

    // 게시판 생성
    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request) {
        Member ownerAccount = memberService.findByMemberId(request.getOwnerId());

        Board newBoard = request.toEntity(ownerAccount);
        boardRepository.save(newBoard);
        return BoardResponse.from(newBoard);
    }

    // 게시판 조회
    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId) {
        Board board = findByBoardId(boardId);
        return BoardResponse.from(board);
    }

    // 게시판 주인 수정
    @Transactional
    public BoardResponse updateBoardOwner(Long boardId, Long memberId, @Valid BoardOwnerUpdateRequest request) {
        Board board = findByBoardId(boardId);
        Member member = memberService.findByMemberId(memberId);
        Member newOwner = memberService.findByMemberId(request.getOwnerId());

        // 게시판 주인 = 계정 사용자 검증
        authorizeBoardOwner(board, member);
        board.changeOwner(newOwner);

        return BoardResponse.from(board);
    }

    // 게시판 삭제
    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = findByBoardId(boardId);
        Member member = memberService.findByMemberId(memberId);

        // 게시판 주인 = 계정 사용자
        authorizeBoardOwner(board, member);
        boardRepository.delete(board);
    }

    private void authorizeBoardOwner(Board board, Member newOwner) {
        if (!board.getOwner().equals(newOwner)) {
            throw new CustomException(ErrorCode.BOARD_ACCOUNT_MISMATCH);
        }
    }

    private Board findByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }
}
