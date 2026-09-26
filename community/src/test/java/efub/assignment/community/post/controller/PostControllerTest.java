package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.post.dto.request.PostUpdateRequest;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockitoBean(types = JpaMetamodelMappingContext.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("게시글 수정 API 테스트")
    void update_post() throws Exception {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        String content = "수정된 게시글 내용입니다.";

        PostUpdateRequest request = new PostUpdateRequest(content);

        PostResponse response = new PostResponse(
                postId,
                new PostResponse.AuthorInfo(memberId, "김이화", false),
                new PostResponse.BoardInfo(1L, "자유게시판"),
                content,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(postService.updatePost(
                eq(postId),
                eq(memberId),
                any(PostUpdateRequest.class)
        )).willReturn(response);

        // when & then
        mockMvc.perform(patch("/posts/{postId}", postId)
                        .header("Auth-id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.author.memberId").value(memberId))
                .andExpect(jsonPath("$.author.authorNickname").value("김이화"))
                .andExpect(jsonPath("$.author.isAnonymous").value(false))
                .andExpect(jsonPath("$.board.boardName").value("자유게시판"));

        verify(postService).updatePost(eq(postId), eq(memberId), any(PostUpdateRequest.class));
    }

    @Test
    @DisplayName("게시글 작성자가 아니면 수정 요청에 실패한다.")
    void update_post_fail_when_not_author() throws Exception {
        // given
        Long postId = 1L;
        Long memberId = 2L;

        PostUpdateRequest request = new PostUpdateRequest("수정된 게시글 내용입니다.");

        given(postService.updatePost(eq(postId), eq(memberId), any(PostUpdateRequest.class)))
                .willThrow(new CustomException(ErrorCode.POST_MEMBER_MISMATCH));

        // when & then
        mockMvc.perform(patch("/posts/{postId}", postId)
                        .header("Auth-id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name="{index}번째 반복-게시글 내용: \"{0}\"  => 수정 요청 실패")
    @ValueSource(strings = {"","1","1234"})
    @DisplayName("게시글 내용이 5자 미만이면 수정 요청에 실패한다.")
    void update_post_fail_when_content_is_too_short(String content) throws Exception {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        PostUpdateRequest request = new PostUpdateRequest(content);

        // when & then
        mockMvc.perform(patch("/posts/{postId}", postId)
                        .header("Auth-id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}