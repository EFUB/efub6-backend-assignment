package efub.assignment.community.board.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.dto.request.CreateBoardRequest;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BoardService boardService;

    private Member createMember() {
        return Member.builder()
                .email("efub@test.com")
                .password("password")
                .nickname("김이화")
                .school("이화여대")
                .studentId("2466000")
                .build();
    }

    private CreateBoardRequest createRequest() {
        return CreateBoardRequest.builder()
                .name("자유게시판")
                .description("자유롭게 작성해주세요~")
                .notification("게시글 유출은 엄격히 금지합니다!")
                .build();
    }

    @Test
    void createBoard_정상적으로_게시판을_생성한다() {
        // given
        Long memberId = 1L;
        Member member = createMember();
        CreateBoardRequest request = createRequest();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(boardRepository.save(any(Board.class))).willAnswer(invocation -> {
            Board savedBoard = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedBoard, "id", 1L);
            return savedBoard;
        });

        // when
        Long boardId = boardService.createBoard(memberId, request);

        // then
        assertEquals(1L, boardId);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void createBoard_존재하지_않는_회원이면_예외가_발생한다() {
        // given
        Long memberId = 999L;
        CreateBoardRequest request = createRequest();

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> boardService.createBoard(memberId, request));
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
        verify(boardRepository, never()).save(any(Board.class));
    }
}