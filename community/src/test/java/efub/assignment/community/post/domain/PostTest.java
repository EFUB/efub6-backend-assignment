package efub.assignment.community.post.domain;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PostTest {

    private Post post;
    private Member member;
    private Board board;

    // 각 테스트 전 새로운 member, board, post 객체 생성
    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@ewha.ac.kr")
                .password("1234")
                .nickname("김남우")
                .university("이화여자대학교")
                .studentId("1111111")
                .build();

        board = Board.builder()
                .owner(member)
                .boardname("게시판1")
                .description("안녕하세요!")
                .notice("존댓말 사용 부탁드립니다~")
                .build();

        post = Post.builder()
                .board(board)
                .writer(member)
                .anonymous(true)
                .content("글 내용")
                .build();
    }

    // Post 객체가 정상적으로 생성되는지 테스트
    // not null 항목 체크 (board, writer, anonymous, content)
    @Test
    void create_post() {
        assertNotNull(post);
        assertEquals(board, post.getBoard());
        assertEquals(member, post.getWriter());
        assertTrue(post.isAnonymous());
        assertEquals("글 내용", post.getContent());
    }

    // changePostContent 메서드 테스트
    // 해당 메서드로 content 수정 시 수정 사항이 반영되는지 확인
    @Test
    void change_content() {
        // when
        post.changePostContent("글 수정");

        // then
        assertEquals("글 수정", post.getContent());
    }
}
