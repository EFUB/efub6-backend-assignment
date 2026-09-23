package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.service.BoardService;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.repositoriy.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private BoardService boardService;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private PostService postService;

    private Member author;
    private Board board;
    PostCreateRequestDto requestDto;
    Long authorId;
    Long boardId;

    @BeforeEach
    void setUp() {
        boardId = 1L;
        authorId = 1L;

        author = Member.builder()
                .email("test@example.com")
                .password("password")
                .nickname("회원1")
                .studentId("1234")
                .university("이화여자대학교")
                .build();
        ReflectionTestUtils.setField(author, "memberId", authorId);

        board = Board.builder()
                .title("게시판1")
                .boardOwner(author)
                .description("게시판 설명")
                .build();
        ReflectionTestUtils.setField(board, "boardId", boardId);

        requestDto = new PostCreateRequestDto(boardId,authorId, "제목", "내용");
    }

    @Test
    @DisplayName("성공 - 게시글 생성")
    void create_post_success() {
        //given
        given(memberService.findByMemberId(authorId)).willReturn(author);
        given(boardService.findByBoardId(boardId)).willReturn(board);

        //when
        PostResponseDto response = postService.createPost(requestDto);

        //then
        assertThat(response).isNotNull();
        assertEquals(boardId, response.boardId());
        assertEquals(authorId, response.accountId());
        assertEquals(requestDto.getTitle(), response.title());
        assertEquals(requestDto.getContent(), response.content());
        assertEquals(0L, response.viewCount());

        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 회원")
    void create_post_memberNotFound_fail() {
        //given
        given(memberService.findByMemberId(authorId)).willThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.createPost(requestDto)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        verify(postRepository, never()).save(any(Post.class));
        verify(memberService).findByMemberId(authorId);
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 게시판")
    void create_post_boardNotFound_fail() {
        //given
        given(boardService.findByBoardId(boardId)).willThrow(new CustomException(ErrorCode.BOARD_NOT_FOUND));

        //when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.createPost(requestDto)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOARD_NOT_FOUND);
        verify(postRepository, never()).save(any(Post.class));
        verify(memberService, never()).findByMemberId(any());
    }
}
