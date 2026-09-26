package efub.assignment.community.post.domain;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PostTest {

    @Test
    @DisplayName("게시글을 생성하면 조회수는 0으로 초기화된다")
    void createPost() {
        Board board = mock(Board.class);
        Member member = mock(Member.class);

        Post post = Post.builder()
                .board(board)
                .member(member)
                .title("테스트 제목")
                .anonymous(false)
                .content("테스트 내용")
                .build();

        assertThat(post.getBoard()).isSameAs(board);
        assertThat(post.getMember()).isSameAs(member);
        assertThat(post.getTitle()).isEqualTo("테스트 제목");
        assertThat(post.getAnonymous()).isFalse();
        assertThat(post.getContent()).isEqualTo("테스트 내용");
        assertThat(post.getViewCount()).isZero();
    }

    @Test
    @DisplayName("게시글을 수정하면 제목과 내용이 변경된다")
    void updatePost() {
        Post post = Post.builder()
                .board(mock(Board.class))
                .member(mock(Member.class))
                .title("수정 전 제목")
                .anonymous(true)
                .content("수정 전 내용")
                .build();

        post.updatePost("수정 후 제목", "수정 후 내용");

        assertThat(post.getTitle()).isEqualTo("수정 후 제목");
        assertThat(post.getContent()).isEqualTo("수정 후 내용");
    }
}
