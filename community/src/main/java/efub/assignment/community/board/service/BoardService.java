package efub.assignment.community.board.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.dto.request.BoardCreateRequest;
import efub.assignment.community.board.dto.request.BoardUpdateRequest;
import efub.assignment.community.board.dto.response.BoardResponseDto;
import efub.assignment.community.board.repositoriy.BoardRepository;
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

    //[게시판 생성]
    @Transactional
    public Long createBoard(BoardCreateRequest request) {
        Member boardOwner = memberService.findByMemberId(request.getMemberId());

        Board newBoard = request.toEntity(boardOwner);
        boardRepository.save(newBoard);

        return newBoard.getBoardId();
    }

    // [게시판 조회]: 게시판 관련 정보 조회?...
    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long boardId) {
        Board board = findByBoardId(boardId);

        return BoardResponseDto.from(board);
    }

    // [게시판 주인 수정]
    @Transactional
    public void updateBoardOwner(Long boardId, Long requesterMemberId, BoardUpdateRequest request) {
        Board board = findByBoardId(boardId);
        Member newOwner = memberService.findByMemberId(request.getMemberId());
        Member requestMember = memberService.findByMemberId(requesterMemberId);

        authorizeBoardOwner(board,requestMember);
        board.changeBoardOwner(newOwner);
    }

    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = findByBoardId(boardId);
        Member member = memberService.findByMemberId(memberId);

        authorizeBoardOwner(board,member);
        boardRepository.delete(board);
    }

    public Board findByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    public void authorizeBoardOwner (Board board, Member member) {
        if(!board.getBoardOwner().getMemberId().equals(member.getMemberId())){
            throw new CustomException(ErrorCode.BOARD_ACCOUNT_MISTMATCH);
        }
    }
}
