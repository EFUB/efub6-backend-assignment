package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequest;
import efub.assignment.community.post.dto.request.PostUpdateRequest;
import efub.assignment.community.post.dto.response.PostResponse;
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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockitoBean(types = JpaMetamodelMappingContext.class)
public class PostControllerTest {

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

    // 게시글 상세 조회 성공
    @Test
    void get_post_success() throws Exception {
        // given
        given(postService.getPost(1L))
                .willReturn(PostResponse.from(testPost));

        // when & then
        mockMvc.perform(get("/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(testPost.getContent()));

        verify(postService).getPost(1L);
    }

    // 게시글 수정 성공
    @Test
    void update_post_success() throws Exception {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        String updatedContent = "수정된 글";

        Post updatedPost = Post.builder()
                .board(testBoard)
                .writer(testMember)
                .anonymous(true)
                .content(updatedContent)
                .build();

        PostUpdateRequest request = new PostUpdateRequest(updatedContent);

        given(postService.updatePostContent(eq(postId), eq(memberId), any(PostUpdateRequest.class)))
                .willReturn(PostResponse.from(updatedPost));

        // when & then
        mockMvc.perform(patch("/posts/{postId}", postId)
                .header("Auth-Id", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(updatedContent));

        verify(postService).updatePostContent(eq(postId), eq(memberId), any(PostUpdateRequest.class));
    }

    // 게시글 수정 요청 시 Auth-Id 누락 예외 처리
    @Test
    void throw_exception_when_header_not_exist_in_update() throws Exception {
        // given
        Long postId = 1L;
        String updatedContent = "수정된 글";

        PostUpdateRequest request = new PostUpdateRequest(updatedContent);

        // when & then
        mockMvc.perform(patch("/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MissingRequestHeaderException.class));

        verifyNoInteractions(postService);
    }

    // 게시글 수정 요청 시 PostUpdateRequest.content null, 빈 칸, 공백, MAX 초과 예외 처리
    @ParameterizedTest
    @MethodSource("invalidContents")
    void throw_exception_when_content_not_valid(String content) throws Exception {
        // given
        PostUpdateRequest request = new PostUpdateRequest(content);

        // when & then
        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .header("Auth-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MethodArgumentNotValidException.class));

        verifyNoInteractions(postService);
    }

    // 게시글 삭제 성공
    @Test
    void delete_post_success() throws Exception {
        // given
        Long memberId = 1L;
        Long postId = 1L;

        // when & then
        mockMvc.perform(delete("/posts/{postId}", postId)
                        .header("Auth-Id", memberId))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(postId, memberId);
    }

    // 게시글 삭제 요청 시 Auth-Id 누락 예외 처리
    @Test
    void throw_exception_when_header_not_exist_in_delete() throws Exception {
        // given
        Long postId = 1L;

        // when & then
        mockMvc.perform(delete("/posts/{postId}", postId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MissingRequestHeaderException.class));

        verifyNoInteractions(postService);
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
