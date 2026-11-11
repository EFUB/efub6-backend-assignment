package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequest;
import efub.assignment.community.post.dto.request.PostUpdateRequest;
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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired PostRepository postRepository;

    private Member testMember;
    private Board testBoard;
    private Post testPost;

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

        testPost = postRepository.save(Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(true)
                .content("글 내용")
                .build());
    }

    @Test
    @DisplayName("GET /posts/{postId} -> 200 & 응답 필드 검증")
    void get_post_success() throws Exception {
        mockMvc.perform(get("/posts/{postId}", testPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("글 내용"));
    }

    @Test
    @DisplayName("PATCH /posts/{postId} -> 200 & DB 반영")
    void update_post_success() throws Exception {
        // given
        PostUpdateRequest request = new PostUpdateRequest("수정된 글");

        // when
        mockMvc.perform(patch("/posts/{postId}", testPost.getId())
                        .header("Auth-Id", testMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 글"));

        // then
        assertThat(findPostFromDb().getContent()).isEqualTo("수정된 글");
    }

    @Test
    @DisplayName("PATCH /posts/{postId} Auth-Id 누락 -> 400 & 수정 x")
    void throw_exception_when_header_not_exist_in_update() throws Exception {
        PostUpdateRequest request = new PostUpdateRequest("수정된 글");

        mockMvc.perform(patch("/posts/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MissingRequestHeaderException.class));

        assertThat(findPostFromDb().getContent()).isEqualTo("글 내용");
    }

    @ParameterizedTest
    @MethodSource("invalidContents")
    @DisplayName("PATCH /posts/{postId} 잘못된 content -> 400 & 수정 x")
    void throw_exception_when_content_not_valid(String content) throws Exception {
        PostUpdateRequest request = new PostUpdateRequest(content);

        mockMvc.perform(patch("/posts/{postId}", testPost.getId())
                        .header("Auth-Id", testMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MethodArgumentNotValidException.class));

        assertThat(findPostFromDb().getContent()).isEqualTo("글 내용");
    }

    @Test
    @DisplayName("DELETE /posts/{postId} -> 204 & DB에서 삭제")
    void delete_post_success() throws Exception {
        Long postId = testPost.getId();

        mockMvc.perform(delete("/posts/{postId}", postId)
                        .header("Auth-Id", testMember.getMemberId()))
                .andExpect(status().isNoContent());

        em.flush();
        em.clear();
        assertThat(postRepository.findById(postId)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /posts/{postId} Auth-Id 누락 -> 400 & 삭제 x")
    void throw_exception_when_header_not_exist_in_delete() throws Exception {
        mockMvc.perform(delete("/posts/{postId}", testPost.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MissingRequestHeaderException.class));

        em.flush();
        em.clear();
        assertThat(postRepository.findById(testPost.getId())).isPresent();
    }

    // flush/clear 후 DB에서 testPost 재조회
    private Post findPostFromDb() {
        em.flush();
        em.clear();
        return postRepository.findById(testPost.getId()).orElseThrow();
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
