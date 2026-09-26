package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.service.BoardService;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostUpdateRequest;
import efub.assignment.community.post.repository.PostLikeRepository;
import efub.assignment.community.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private BoardService boardService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("작성자는 게시글 내용을 수정할 수 있다.")
    void update_post() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        Member author = mock(Member.class);
        given(author.getMemberId()).willReturn(memberId);

        Board board = Board.builder()
                .boardName("자유게시판")
                .owner(author)
                .build();

        Post post = Post.builder()
                .author(author)
                .board(board)
                .isAnonymous(false)
                .content("기존 게시글 내용입니다.")
                .build();

        PostUpdateRequest request = new PostUpdateRequest("수정된 게시글 내용입니다.");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        given(memberService.findByMemberId(memberId)).willReturn(author);

        // when
        postService.updatePost(postId, memberId, request);

        // then
        assertEquals("수정된 게시글 내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("작성자가 아니면 게시글을 수정할 수 없다.")
    void update_post_fail_when_not_author() {
        // given
        Long postId = 1L;
        Long memberId = 2L;

        Member author = mock(Member.class);
        Member otherMember = mock(Member.class);

        given(author.getMemberId()).willReturn(1L);
        given(otherMember.getMemberId()).willReturn(2L);

        Board board = Board.builder()
                .boardName("자유게시판")
                .owner(author)
                .build();

        Post post = Post.builder()
                .author(author)
                .board(board)
                .isAnonymous(false)
                .content("기존 게시글 내용입니다.")
                .build();

        PostUpdateRequest request = new PostUpdateRequest("수정된 게시글 내용입니다.");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        given(memberService.findByMemberId(memberId)).willReturn(otherMember);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> postService.updatePost(postId, memberId, request)
        );

        // then
        assertEquals(ErrorCode.POST_MEMBER_MISMATCH, exception.getErrorCode());
        assertEquals("기존 게시글 내용입니다.", post.getContent());
    }
}