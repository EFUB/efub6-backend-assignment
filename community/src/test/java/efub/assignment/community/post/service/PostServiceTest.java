package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.service.BoardService;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostUpdateRequest;
import efub.assignment.community.post.dto.response.PostListResponse;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    // MemberRepository, PostRepository, BoardRepository Mock 객체로 생성
    @Mock
    private MemberService memberService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardService boardService;

    // 테스트 대상은 mock으로 만들면 실제 로직이 전혀 실행되지 않음
    // 진짜 객체에 mock 의존성을 주입하기 위해 @InjectMocks 사용
    @InjectMocks
    private PostService postService;

    private Member testMember;
    private Board testBoard;
    private Post testPost;

    // 각 테스트 실행 전 validator, 테스트용 member, post, board 객체 초기화
    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@ewha.ac.kr")
                .password("1234")
                .nickname("김남우")
                .university("이화여자대학교")
                .studentId("1111111")
                .build();

        testBoard = Board.builder()
                .owner(testMember)
                .boardname("게시판1")
                .description("안녕하세요!")
                .notice("존댓말 사용 부탁드립니다~")
                .build();

        testPost = Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(true)
                .content("글 내용")
                .build();
    }

    // 존재하지 않는 post 조회 시 예외 처리
    @Test
    void throw_exception_when_not_exist() {
        // given
        given(postRepository.findById(2L)).willReturn(Optional.empty());

        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.getPost(2L)
        );
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    // 게시글 조회 시 올바른 내용 반환
    @Test
    void return_correct_post() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        // when
        PostResponse response = postService.getPost(1L);

        // then
        assertEquals(testPost.getContent(), response.content());
    }

    // 존재하지 않는 게시판의 게시글 목록 조회 시 예외 처리
    @Test
    void throw_exception_when_board_not_exist() {
        // given
        given(boardService.findByBoardId(2L)).willThrow(new CustomException(ErrorCode.BOARD_NOT_FOUND));
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.getAllPosts(2L)
        );

        // then
        assertEquals(ErrorCode.BOARD_NOT_FOUND, exception.getErrorCode());
    }

    // 게시판의 게시글 목록 조회 시 PostList 반환
    @Test
    void return_board_post_list() {
        // given
        Post post1 = Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(true)
                .content("글 내용1")
                .build();
        Post post2 = Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(false)
                .content("글 내용2")
                .build();

        given(boardService.findByBoardId(1L)).willReturn(testBoard);
        given(postRepository.findAllByBoardIdOrderByCreatedAtDesc(1L)).willReturn(List.of(post1, post2));

        // when
        PostListResponse response = postService.getAllPosts(1L);

        // then
        assertEquals(2L, response.totalPosts());
        assertEquals("글 내용1", response.posts().get(0).content());
        assertEquals("글 내용2", response.posts().get(1).content());
    }

    // 존재하지 않는 게시글 수정 요청 시 예외 처리
    @Test
    void throw_exception_when_post_not_exist_in_update() {
        // given
        PostUpdateRequest request = new PostUpdateRequest("수정된 글");
        given(memberService.findByMemberId(1L)).willReturn(testMember);
        given(postRepository.findById(2L)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.updatePostContent(2L, 1L, request)
        );

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    // 수정 요청자가 존재하지 않는 회원일 경우 예외 처리
    @Test
    void throw_exception_when_member_not_exist_in_update() {
        PostUpdateRequest request = new PostUpdateRequest("수정된 글");
        given(memberService.findByMemberId(1L)).willThrow(new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.updatePostContent(1L, 1L, request)
        );

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    // 게시글 작성자가 아닌 사용자가 수정 요청 시 예외 처리
    @Test
    void throw_exception_when_not_authorized_in_update() {
        // given
        Member otherMember = createOtherMember();

        PostUpdateRequest request = new PostUpdateRequest("수정된 글");
        given(memberService.findByMemberId(2L)).willReturn(otherMember);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.updatePostContent(1L, 2L, request)
        );

        assertEquals(ErrorCode.POST_ACCOUNT_MISMATCH, exception.getErrorCode());
        assertEquals("글 내용", testPost.getContent());
    }

    // 게시글 수정 성공
    @Test
    void update_post() {
        // given
        PostUpdateRequest request = new PostUpdateRequest("수정된 글");
        given(memberService.findByMemberId(1L)).willReturn(testMember);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        // when
        postService.updatePostContent(1L, 1L, request);

        // then
        assertEquals("수정된 글", testPost.getContent());
    }

    // 존재하지 않는 사용자의 게시글 삭제 요청 시 예외 처리
    @Test
    void throw_exception_when_member_not_exist_in_delete() {
        // given
        given(memberService.findByMemberId(2L)).willThrow(new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.deletePost(1L, 2L)
        );

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, never()).delete(any(Post.class));
    }

    // 존재하지 않는 게시글 삭제 요청 시 예외 처리
    @Test
    void throw_exception_when_post_not_found_in_delete() {
        // given
        given(memberService.findByMemberId(1L)).willReturn(testMember);
        given(postRepository.findById(2L)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.deletePost(2L, 1L)
        );

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, never()).delete(any(Post.class));
    }

    // 게시글 작성자 이외의 게시글 삭제 요쳥 시 예외 처리
    @Test
    void throw_exception_when_not_authorized_in_delete() {
        // given
        Member otherMember = createOtherMember();

        given(memberService.findByMemberId(2L)).willReturn(otherMember);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> postService.deletePost(1L, 2L)
        );

        assertEquals(ErrorCode.POST_ACCOUNT_MISMATCH, exception.getErrorCode());
        verify(postRepository, never()).delete(any(Post.class));
    }

    // 게시글 삭제 성공
    @Test
    void delete_post() {
        // given
        given(memberService.findByMemberId(1L)).willReturn(testMember);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        // when
        postService.deletePost(1L, 1L);

        // then
        verify(postRepository).delete(testPost);
    }

    private Member createOtherMember() {
        return Member.builder()
                .email("other@ewha.ac.kr")
                .password("2134")
                .nickname("타인")
                .university("이화여자대학교")
                .studentId("2222222")
                .build();
    }
}
