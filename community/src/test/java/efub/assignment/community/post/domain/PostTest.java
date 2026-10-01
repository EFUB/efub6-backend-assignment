package efub.assignment.community.post.domain;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostTest {

    private Post post;
    private Member author;
    private Board board;

    @BeforeEach
    void setUp() {
        author = Member.builder()
                .email("test@example.com")
                .password("password")
                .nickname("회원1")
                .studentId("1234")
                .university("이화여자대학교")
                .build();

        board = Board.builder()
                .title("게시판1")
                .boardOwner(author)
                .description("게시판 설명")
                .build();

        post = Post.builder()
                .title("게시글 제목1")
                .content("게시글 내용1")
                .writer(author)
                .board(board)
                .build();

    }

    @Test
    @DisplayName("게시글 생성")
    void create_post() {
        assertNotNull(post);

        assertEquals("게시글 제목1", post.getTitle());
        assertEquals("게시글 내용1", post.getContent());
        assertEquals(author, post.getWriter());
        assertEquals(board, post.getBoard());
    }

    @Test
    @DisplayName("게시글 생성 시 조회수 값은 0")
    void create_post_initialize_viewCount() {
        assertEquals(0L, post.getViewCount());
    }

    @Test
    @DisplayName("게시글 내용 수정")
    void change_content() {
        //when
        post.changeContent("내용 수정");

        //then
        assertEquals("내용 수정", post.getContent());
        assertEquals("게시글 제목1", post.getTitle());
        assertEquals(author, post.getWriter());
        assertEquals(board, post.getBoard());
    }
}
