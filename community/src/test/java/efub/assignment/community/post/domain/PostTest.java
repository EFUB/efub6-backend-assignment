package efub.assignment.community.post.domain;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostTest {

    private Post post;

    //given & when
    @BeforeEach
    void setUp() {
        Member author = Member.builder()
                .nickname("김이화")
                .build();

        Board board = Board.builder()
                .boardName("자유게시판")
                .owner(author)
                .build();

        post = Post.builder()
                .author(author)
                .board(board)
                .isAnonymous(false)
                .content("기존 게시글 내용입니다.")
                .build();
    }

    @Test
    @DisplayName("게시글 생성 테스트")
    void create_post() {
        //then
        assertNotNull(post);
        assertNotNull(post.getAuthor());
        assertNotNull(post.getBoard());
        assertEquals(false, post.getIsAnonymous());
        assertEquals("기존 게시글 내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void change_content() {
        //given
        String newContent = "수정된 게시글 내용입니다.";

        //when
        post.changeContent(newContent);

        //then
        assertEquals("수정된 게시글 내용입니다.", post.getContent());
    }
}