package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequest;
import efub.assignment.community.post.dto.response.PostListResponse;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.dto.summary.PostSummary;
import efub.assignment.community.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BoardPostController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockitoBean(types = JpaMetamodelMappingContext.class)
public class BoardPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    private Member testMember;
    private Board testBoard;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@ewha.ac.kr")
                .password("1234")
                .nickname("김남우")
                .university("이화여자대학교")
                .studentId("1111111")
                .build();

        testBoard = Board.builder()
                .owner(testMember)
                .boardname("게시판1")
                .description("안녕하세요!")
                .notice("존댓말 사용 부탁드립니다~")
                .build();

        testPost = Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(true)
                .content("글 내용")
                .build();
    }

    // 게시글 생성 API 테스트
    @Test
    void create_post_success() throws Exception {
        // given
        Long boardId = 1L;
        Long memberId = 1L;
        boolean anonymous = true;
        String content = "글 내용";

        PostCreateRequest request = PostCreateRequest.builder()
                .anonymous(anonymous)
                .content(content)
                .build();

        given(postService.createPost(eq(boardId), eq(memberId), any(PostCreateRequest.class))).willReturn(PostResponse.from(testPost));

        // when & then
        mockMvc.perform(post("/boards/{boardId}/posts", 1L)
                        .header("Auth-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(content));
        verify(postService).createPost(eq(boardId), eq(memberId), any(PostCreateRequest.class));
    }

    // Auth-Id 헤더가 없을 시 예외 처리
    @Test
    void throw_exception_when_header_not_exist() throws Exception {
        // given
        boolean anonymous = true;
        String content = "글 내용";

        PostCreateRequest request = PostCreateRequest.builder()
                .anonymous(anonymous)
                .content(content)
                .build();

        // when & then
       mockMvc.perform(post("/boards/{boardId}/posts", 1L)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(result -> assertThat(result.getResolvedException())
                       .isInstanceOf(MissingRequestHeaderException.class));

        verifyNoInteractions(postService);
    }

    // content가 null, 빈 칸, 공백, max 초과일 경우 예외 처리
    @ParameterizedTest
    @MethodSource("invalidContents")
    void throw_exception_when_content_not_valid(String content) throws Exception {
        // given
        boolean anonymous = true;

        PostCreateRequest request = PostCreateRequest.builder()
                .anonymous(anonymous)
                .content(content)
                .build();

        // when & then
        mockMvc.perform(post("/boards/{boardId}/posts", 1L)
                        .header("Auth-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MethodArgumentNotValidException.class));

        verifyNoInteractions(postService);
    }

    @Test
    void get_all_posts_success() throws Exception {
        // given
        Post post1 = Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(true)
                .content("글 내용1")
                .build();
        Post post2 = Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(true)
                .content("글 내용2")
                .build();
        PostSummary postSummary1 = PostSummary.from(post1);
        PostSummary postSummary2 = PostSummary.from(post2);

        given(postService.getAllPosts(1L))
                .willReturn(new PostListResponse(List.of(postSummary1, postSummary2), 2L));

        // when & then
        mockMvc.perform(get("/boards/{boardId}/posts", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[0].content").value("글 내용1"))
                .andExpect(jsonPath("$.posts[1].content").value("글 내용2"));

        verify(postService).getAllPosts(1L);
    }

    // PostCreateRequest.content 테스트 항목
    static Stream<Named<String>> invalidContents() {
        return Stream.of(
                Named.of("null", null),
                Named.of("빈 문자열", ""),
                Named.of("공백", " "),
                Named.of("MAX + 1", "가".repeat(PostCreateRequest.MAX_CONTENT_LENGTH + 1))
        );
    }
}
