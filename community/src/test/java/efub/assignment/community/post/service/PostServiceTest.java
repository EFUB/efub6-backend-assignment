package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.CreatePostRequestDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.repository.PostLikeRepository;
import efub.assignment.community.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시판과 회원이 존재하면 게시글을 생성한다")
    void createPost() {
        Long boardId = 1L;
        Long memberId = 2L;
        Board board = org.mockito.Mockito.mock(Board.class);
        Member member = org.mockito.Mockito.mock(Member.class);
        CreatePostRequestDto request = new CreatePostRequestDto(
                "테스트 제목",
                false,
                "테스트 내용"
        );

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(board.getBoardId()).thenReturn(boardId);
        when(member.getMemberId()).thenReturn(memberId);
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(postLikeRepository.countByPost(any(Post.class))).thenReturn(0L);

        PostResponseDto result = postService.createPost(boardId, memberId, request);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getBoard()).isSameAs(board);
        assertThat(savedPost.getMember()).isSameAs(member);
        assertThat(savedPost.getTitle()).isEqualTo("테스트 제목");
        assertThat(savedPost.getContent()).isEqualTo("테스트 내용");
        assertThat(savedPost.getViewCount()).isZero();
        assertThat(result.boardId()).isEqualTo(boardId);
        assertThat(result.memberId()).isEqualTo(memberId);
        assertThat(result.likeCount()).isZero();
    }

    @Test
    @DisplayName("존재하지 않는 게시판에는 게시글을 생성할 수 없다")
    void createPostWithMissingBoard() {
        Long boardId = 999L;
        CreatePostRequestDto request = new CreatePostRequestDto(
                "테스트 제목",
                false,
                "테스트 내용"
        );
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(boardId, 2L, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BOARD_NOT_FOUND);

        verify(memberRepository, never()).findById(any());
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("키워드가 제목 또는 내용에 포함된 게시글을 검색한다")
    void searchPosts() {
        Long boardId = 1L;
        Board board = org.mockito.Mockito.mock(Board.class);
        Post firstPost = createPost(board, "스프링 테스트", "단위 테스트를 작성합니다.");
        Post secondPost = createPost(board, "TDD 공부", "스프링으로 검색 기능을 만듭니다.");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(postRepository.searchByBoardIdAndKeyword(boardId, "스프링"))
                .thenReturn(List.of(firstPost, secondPost));
        when(postLikeRepository.countByPost(firstPost)).thenReturn(3L);
        when(postLikeRepository.countByPost(secondPost)).thenReturn(1L);

        PostListResponseDto result = postService.searchPosts(boardId, "  스프링  ");

        assertThat(result.totalPosts()).isEqualTo(2L);
        assertThat(result.posts())
                .extracting("title")
                .containsExactly("스프링 테스트", "TDD 공부");
        assertThat(result.posts())
                .extracting("likeCount")
                .containsExactly(3L, 1L);
        verify(postRepository).searchByBoardIdAndKeyword(boardId, "스프링");
    }

    @Test
    @DisplayName("검색어가 비어 있으면 게시글을 검색할 수 없다")
    void searchPostsWithBlankKeyword() {
        assertThatThrownBy(() -> postService.searchPosts(1L, "   "))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT);

        verify(boardRepository, never()).findById(any());
        verify(postRepository, never()).searchByBoardIdAndKeyword(any(), any());
    }

    private Post createPost(Board board, String title, String content) {
        return Post.builder()
                .board(board)
                .member(org.mockito.Mockito.mock(Member.class))
                .title(title)
                .anonymous(false)
                .content(content)
                .build();
    }
}
