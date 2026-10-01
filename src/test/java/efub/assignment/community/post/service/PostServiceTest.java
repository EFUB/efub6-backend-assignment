package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.PostLike;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.repository.PostLikeRepository;
import efub.assignment.community.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostService postService;

    private Member createMember() {
        return Member.builder()
                .email("efub@test.com")
                .password("password")
                .nickname("김이화")
                .school("이화여대")
                .studentId("2020000000")
                .build();
    }

    private Post createPost(Member writer) {
        return Post.builder()
                .board(null)
                .writer(writer)
                .title("제목")
                .content("본문")
                .isAnonymous(false)
                .build();
    }

    private Board createBoard(Member writer) {
        return Board.builder()
                .writer(writer)
                .name("자유게시판")
                .description("설명")
                .notification("공지")
                .build();
    }

    private Post createPost(Member writer, Board board) {
        return Post.builder()
                .board(board)
                .writer(writer)
                .title("제목")
                .content("본문")
                .isAnonymous(false)
                .build();
    }

    @Test
    void likePost_정상적으로_좋아요에_성공한다() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member member = createMember();
        Post post = createPost(member);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postLikeRepository.existsByPostAndMember(post, member)).willReturn(false);

        // when
        PostResponse response = postService.likePost(postId, memberId);

        // then
        assertNotNull(response);
        assertEquals(1L, post.getLikeCount());
        verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    void likePost_이미_좋아요한_게시글에_중복_좋아요_시도시_예외가_발생한다() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member member = createMember();
        Board board = createBoard(member);
        Post post = createPost(member, board);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postLikeRepository.existsByPostAndMember(post, member)).willReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.likePost(postId, memberId));
        assertEquals(ErrorCode.POST_LIKE_ALREADY_EXISTS, exception.getErrorCode());
        verify(postLikeRepository, never()).save(any(PostLike.class));
    }

    @Test
    void likePost_존재하지_않는_게시글이면_예외가_발생한다() {
        // given
        Long postId = 999L;
        Long memberId = 1L;

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.likePost(postId, memberId));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void unlikePost_정상적으로_좋아요_취소에_성공한다() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member member = createMember();
        Board board = createBoard(member);
        Post post = createPost(member, board);
        post.increaseLikeCount(); // 좋아요가 되어 있는 상태로 세팅
        PostLike postLike = PostLike.builder().post(post).member(member).build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postLikeRepository.findByPostAndMember(post, member)).willReturn(Optional.of(postLike));

        // when
        PostResponse response = postService.unlikePost(postId, memberId);

        // then
        assertNotNull(response);
        assertEquals(0L, post.getLikeCount());
        verify(postLikeRepository).delete(postLike);
    }

    @Test
    void unlikePost_좋아요_기록이_없으면_예외가_발생한다() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member member = createMember();
        Board board = createBoard(member);
        Post post = createPost(member, board);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postLikeRepository.findByPostAndMember(post, member)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.unlikePost(postId, memberId));
        assertEquals(ErrorCode.POST_LIKE_NOT_FOUND, exception.getErrorCode());
        verify(postLikeRepository, never()).delete(any(PostLike.class));
    }

    @Test
    void unlikePost_존재하지_않는_게시글이면_예외가_발생한다() {
        // given (아까 확인한 추가 케이스)
        Long postId = 999L;
        Long memberId = 1L;

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.unlikePost(postId, memberId));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }
}