package efub.assignment.community.post.repository;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.global.config.QuerydslConfig;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostSearchCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("키워드가 제목 또는 내용에 포함된 게시글만 조회한다")
    void searchByKeyword() {
        Member writer = saveMember("writer", "writer@example.com");
        Board targetBoard = saveBoard(writer, "개발 게시판");
        Board otherBoard = saveBoard(writer, "다른 게시판");

        savePost(targetBoard, writer, "Spring QueryDSL", "동적 쿼리를 구현합니다.", false);
        savePost(targetBoard, writer, "JPA 검색", "spring으로 검색합니다.", false);
        savePost(targetBoard, writer, "일상 이야기", "오늘의 기록", false);
        savePost(otherBoard, writer, "Spring 소식", "다른 게시판의 글", false);
        flushAndClear();

        List<Post> result = postRepository.searchByConditions(
                targetBoard.getBoardId(),
                new PostSearchCondition("  SPRING  ", null)
        );

        assertThat(result)
                .extracting(Post::getTitle)
                .containsExactlyInAnyOrder("Spring QueryDSL", "JPA 검색");
    }

    @Test
    @DisplayName("작성자 닉네임으로 검색할 때 익명 게시글은 노출하지 않는다")
    void searchByWriterExcludesAnonymousPosts() {
        Member kim = saveMember("KimWriter", "kim@example.com");
        Member lee = saveMember("LeeWriter", "lee@example.com");
        Board board = saveBoard(kim, "자유 게시판");

        savePost(board, kim, "공개 글", "공개 내용", false);
        savePost(board, kim, "익명 글", "익명 내용", true);
        savePost(board, lee, "다른 작성자 글", "다른 내용", false);
        flushAndClear();

        List<Post> result = postRepository.searchByConditions(
                board.getBoardId(),
                new PostSearchCondition(null, "kim")
        );

        assertThat(result)
                .extracting(Post::getTitle)
                .containsExactly("공개 글");
    }

    @Test
    @DisplayName("키워드와 작성자 조건은 AND로 조합한다")
    void searchByKeywordAndWriter() {
        Member kim = saveMember("KimWriter", "kim@example.com");
        Member lee = saveMember("LeeWriter", "lee@example.com");
        Board board = saveBoard(kim, "자유 게시판");

        savePost(board, kim, "QueryDSL 공부", "동적 검색", false);
        savePost(board, kim, "JPA 공부", "정적 검색", false);
        savePost(board, lee, "QueryDSL 질문", "검색 질문", false);
        flushAndClear();

        List<Post> result = postRepository.searchByConditions(
                board.getBoardId(),
                new PostSearchCondition("QueryDSL", "KimWriter")
        );

        assertThat(result)
                .extracting(Post::getTitle)
                .containsExactly("QueryDSL 공부");
    }

    private Member saveMember(String nickname, String email) {
        Member member = Member.builder()
                .studentId(email)
                .university("이화여자대학교")
                .nickname(nickname)
                .email(email)
                .password("password123!")
                .build();
        entityManager.persist(member);
        return member;
    }

    private Board saveBoard(Member owner, String boardName) {
        Board board = Board.builder()
                .member(owner)
                .boardName(boardName)
                .description("게시판 설명")
                .notice("게시판 공지")
                .build();
        entityManager.persist(board);
        return board;
    }

    private void savePost(
            Board board,
            Member writer,
            String title,
            String content,
            boolean anonymous
    ) {
        Post post = Post.builder()
                .board(board)
                .member(writer)
                .title(title)
                .anonymous(anonymous)
                .content(content)
                .build();
        entityManager.persist(post);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
