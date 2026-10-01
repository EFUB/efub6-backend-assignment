package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequest;
import efub.assignment.community.post.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class BoardPostControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired PostRepository postRepository;

    private Member testMember;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(Member.builder()
                .email("test@ewha.ac.kr")
                .password("1234")
                .nickname("김남우")
                .university("이화여자대학교")
                .studentId("1111111")
                .build());

        testBoard = boardRepository.save(Board.builder()
                .owner(testMember)
                .boardname("게시판1")
                .description("안녕하세요!")
                .notice("존댓말 사용 부탁드립니다~")
                .build());
    }

    @Test
    @DisplayName("POST /boards/{boardId}/posts -> 201 & 응답 필드 검증 & H2 저장")
    void create_post_success() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .anonymous(true)
                .content("글 내용")
                .build();

        // when
        mockMvc.perform(post("/boards/{boardId}/posts", testBoard.getId())
                        .header("Auth-Id", testMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("글 내용"));

        // then
        em.flush();
        em.clear();

        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(1);

        Post savedPost = posts.get(0);
        assertThat(savedPost.getContent()).isEqualTo("글 내용");
        assertThat(savedPost.isAnonymous()).isTrue();
        assertThat(savedPost.getWriter().getMemberId()).isEqualTo(testMember.getMemberId());
        assertThat(savedPost.getBoard().getId()).isEqualTo(testBoard.getId());
    }

    @Test
    @DisplayName("POST /boards/{boardId}/posts Auth-Id 헤더 없음 -> 400 & 저장 x")
    void throw_exception_when_header_not_exist() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .anonymous(true)
                .content("글 내용")
                .build();

        // when & then
        mockMvc.perform(post("/boards/{boardId}/posts", testBoard.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MissingRequestHeaderException.class));

        assertThat(postRepository.count()).isZero();
    }

    @ParameterizedTest
    @MethodSource("invalidContents")
    @DisplayName("POST /boards/{boardId}/posts 잘못된 content -> 400 & 저장 x")
    void throw_exception_when_content_not_valid(String content) throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .anonymous(true)
                .content(content)
                .build();

        // when & then
        mockMvc.perform(post("/boards/{boardId}/posts", testBoard.getId())
                        .header("Auth-Id", testMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MethodArgumentNotValidException.class));

        assertThat(postRepository.count()).isZero();
    }

    @Test
    @DisplayName("GET /boards/{boardId}/posts -> 200 & 게시글 목록")
    void get_all_posts_success() throws Exception {
        // given
        postRepository.save(Post.builder()
                .board(testBoard).writer(testMember).anonymous(true).content("글 내용1").build());
        postRepository.save(Post.builder()
                .board(testBoard).writer(testMember).anonymous(true).content("글 내용2").build());

        // when & then
        mockMvc.perform(get("/boards/{boardId}/posts", testBoard.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[*].content",
                        containsInAnyOrder("글 내용1", "글 내용2")));
    }

    static Stream<Named<String>> invalidContents() {
        return Stream.of(
                Named.of("null", null),
                Named.of("빈 문자열", ""),
                Named.of("공백", " "),
                Named.of("MAX + 1", "가".repeat(PostCreateRequest.MAX_CONTENT_LENGTH + 1))
        );
    }
}
