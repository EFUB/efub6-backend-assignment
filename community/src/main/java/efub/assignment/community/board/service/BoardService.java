package efub.assignment.community.board.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.dto.request.BoardCreateRequest;
import efub.assignment.community.board.dto.request.BoardOwnerUpdateRequest;
import efub.assignment.community.board.dto.response.BoardCreateResponse;
import efub.assignment.community.board.dto.response.BoardGetResponse;
import efub.assignment.community.board.dto.response.BoardOwnerUpdateResponse;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;

    @Transactional
    public BoardCreateResponse createBoard(Long memberId, BoardCreateRequest request){
        Member owner = memberService.findByMemberId(memberId);

        Board newBoard = request.toEntity(owner);

        boardRepository.save(newBoard);
        return BoardCreateResponse.from(newBoard);
    }

    @Transactional
    public BoardOwnerUpdateResponse changeBoardOwner(Long authMemberId, Long boardId, BoardOwnerUpdateRequest request) {
        Board board = findByBoardId(boardId);

        // 현재 요청자가 게시판 주인인지 확인
        if (!board.getOwner().getMemberId().equals(authMemberId)) {
            throw new CustomException(ErrorCode.BOARD_ACCESS_DENIED);
        }

        // 새 주인이 될 멤버 조회
        Member newOwner = memberService.findByMemberId(request.getMemberId());

        board.changeOwner(newOwner);

        return BoardOwnerUpdateResponse.from(board);
    }

    @Transactional(readOnly = true)
    public BoardGetResponse getBoard(Long boardId, Long memberId) {
        Board board = findByBoardId(boardId);
        Member member = memberService.findByMemberId(memberId);

        boolean canEdit = board.getOwner().getMemberId().equals(member.getMemberId());

        return BoardGetResponse.from(board, canEdit);
    }

    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = findByBoardId(boardId);

        // 현재 요청자가 게시판 주인인지 확인
        if (!board.getOwner().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.BOARD_ACCESS_DENIED);
        }

        boardRepository.delete(board);
    }

    public Board findByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }
}


