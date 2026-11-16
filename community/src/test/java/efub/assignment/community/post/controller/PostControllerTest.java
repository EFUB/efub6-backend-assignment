package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.security.AuthenticatedMember;
import efub.assignment.community.post.dto.request.CreatePostRequestDto;
import efub.assignment.community.post.dto.request.UpdatePostRequestDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.dto.summary.PostSummaryDto;
import efub.assignment.community.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@ActiveProfiles("test")
class PostControllerTest {

    private static final Long BOARD_ID = 1L;
    private static final Long MEMBER_ID = 2L;
    private static final Long POST_ID = 10L;
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 9, 30, 12, 0);
    private static final LocalDateTime MODIFIED_AT = LocalDateTime.of(2026, 9, 30, 12, 30);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("게시글 생성 성공: 유효한 요청이면 201과 생성된 게시글을 반환한다")
    void createPost_success() throws Exception {
        CreatePostRequestDto request = new CreatePostRequestDto("테스트 제목", false, "테스트 내용");
        PostResponseDto response = postResponse("테스트 제목", "테스트 내용");
        when(postService.createPost(BOARD_ID, MEMBER_ID, request)).thenReturn(response);

        mockMvc.perform(post("/boards/{boardId}/posts", BOARD_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.postId").value(POST_ID))
                .andExpect(jsonPath("$.boardId").value(BOARD_ID))
                .andExpect(jsonPath("$.memberId").value(MEMBER_ID))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"));

        verify(postService).createPost(BOARD_ID, MEMBER_ID, request);
    }

    @Test
    @DisplayName("게시글 생성 실패: 제목이 비어 있으면 400을 반환한다")
    void createPost_fail_blankTitle() throws Exception {
        CreatePostRequestDto request = new CreatePostRequestDto(" ", false, "테스트 내용");

        mockMvc.perform(post("/boards/{boardId}/posts", BOARD_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("title: 제목은 필수입니다."))
                .andExpect(jsonPath("$.path").value("/boards/1/posts"));

        verify(postService, never()).createPost(any(), any(), any());
    }

    @Test
    @DisplayName("게시판별 게시글 조회 성공: 200과 게시글 목록을 반환한다")
    void getPostsByBoard_success() throws Exception {
        PostSummaryDto summary = new PostSummaryDto(POST_ID, "테스트 제목", false, 3L, 2L);
        PostListResponseDto response = new PostListResponseDto(List.of(summary), 1L);
        when(postService.getPostsByBoard(BOARD_ID)).thenReturn(response);

        mockMvc.perform(get("/boards/{boardId}/posts", BOARD_ID)
                        .with(authenticatedMember(MEMBER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPosts").value(1))
                .andExpect(jsonPath("$.posts[0].postId").value(POST_ID))
                .andExpect(jsonPath("$.posts[0].title").value("테스트 제목"))
                .andExpect(jsonPath("$.posts[0].likeCount").value(2));

        verify(postService).getPostsByBoard(BOARD_ID);
    }

    @Test
    @DisplayName("게시판별 게시글 조회 실패: 게시판이 없으면 404를 반환한다")
    void getPostsByBoard_fail_boardNotFound() throws Exception {
        when(postService.getPostsByBoard(999L))
                .thenThrow(new CustomException(ErrorCode.BOARD_NOT_FOUND));

        mockMvc.perform(get("/boards/{boardId}/posts", 999L)
                        .with(authenticatedMember(MEMBER_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value("BOARD_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/boards/999/posts"));

        verify(postService).getPostsByBoard(999L);
    }

    @Test
    @DisplayName("게시글 검색 성공: 200과 검색 결과를 반환한다")
    void searchPosts_success() throws Exception {
        PostSummaryDto summary = new PostSummaryDto(POST_ID, "스프링 테스트", false, 5L, 3L);
        PostListResponseDto response = new PostListResponseDto(List.of(summary), 1L);
        when(postService.searchPosts(BOARD_ID, "스프링")).thenReturn(response);

        mockMvc.perform(get("/boards/{boardId}/posts/search", BOARD_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .param("keyword", "스프링"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPosts").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("스프링 테스트"));

        verify(postService).searchPosts(BOARD_ID, "스프링");
    }

    @Test
    @DisplayName("게시글 검색 실패: 검색어가 비어 있으면 400을 반환한다")
    void searchPosts_fail_blankKeyword() throws Exception {
        when(postService.searchPosts(BOARD_ID, "   "))
                .thenThrow(new CustomException(ErrorCode.INVALID_INPUT));

        mockMvc.perform(get("/boards/{boardId}/posts/search", BOARD_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .param("keyword", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.path").value("/boards/1/posts/search"));

        verify(postService).searchPosts(BOARD_ID, "   ");
    }

    @Test
    @DisplayName("게시글 상세 조회 성공: 200과 게시글 정보를 반환한다")
    void getPost_success() throws Exception {
        PostResponseDto response = postResponse("테스트 제목", "테스트 내용");
        when(postService.getPost(POST_ID)).thenReturn(response);

        mockMvc.perform(get("/posts/{postId}", POST_ID)
                        .with(authenticatedMember(MEMBER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(POST_ID))
                .andExpect(jsonPath("$.viewCount").value(3))
                .andExpect(jsonPath("$.likeCount").value(2));

        verify(postService).getPost(POST_ID);
    }

    @Test
    @DisplayName("게시글 상세 조회 실패: 게시글이 없으면 404를 반환한다")
    void getPost_fail_notFound() throws Exception {
        when(postService.getPost(999L))
                .thenThrow(new CustomException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(get("/posts/{postId}", 999L)
                        .with(authenticatedMember(MEMBER_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/posts/999"));

        verify(postService).getPost(999L);
    }

    @Test
    @DisplayName("게시글 수정 성공: 작성자가 요청하면 200과 수정된 게시글을 반환한다")
    void updatePost_success() throws Exception {
        UpdatePostRequestDto request = new UpdatePostRequestDto("수정 제목", "수정 내용");
        PostResponseDto response = postResponse("수정 제목", "수정 내용");
        when(postService.updatePost(POST_ID, MEMBER_ID, request)).thenReturn(response);

        mockMvc.perform(patch("/posts/{postId}", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(POST_ID))
                .andExpect(jsonPath("$.title").value("수정 제목"))
                .andExpect(jsonPath("$.content").value("수정 내용"));

        verify(postService).updatePost(POST_ID, MEMBER_ID, request);
    }

    @Test
    @DisplayName("게시글 수정 실패: 작성자가 아니면 403을 반환한다")
    void updatePost_fail_writerMismatch() throws Exception {
        UpdatePostRequestDto request = new UpdatePostRequestDto("수정 제목", "수정 내용");
        when(postService.updatePost(POST_ID, MEMBER_ID, request))
                .thenThrow(new CustomException(ErrorCode.POST_MEMBER_MISMATCH));

        mockMvc.perform(patch("/posts/{postId}", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.errorCode").value("POST_MEMBER_MISMATCH"));

        verify(postService).updatePost(POST_ID, MEMBER_ID, request);
    }

    @Test
    @DisplayName("게시글 삭제 성공: 작성자가 요청하면 200과 성공 메시지를 반환한다")
    void deletePost_success() throws Exception {
        doNothing().when(postService).deletePost(POST_ID, MEMBER_ID);

        mockMvc.perform(delete("/posts/{postId}", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 삭제되었습니다."));

        verify(postService).deletePost(POST_ID, MEMBER_ID);
    }

    @Test
    @DisplayName("게시글 삭제 실패: 작성자가 아니면 403을 반환한다")
    void deletePost_fail_writerMismatch() throws Exception {
        doThrow(new CustomException(ErrorCode.POST_MEMBER_MISMATCH))
                .when(postService).deletePost(POST_ID, MEMBER_ID);

        mockMvc.perform(delete("/posts/{postId}", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("POST_MEMBER_MISMATCH"));

        verify(postService).deletePost(POST_ID, MEMBER_ID);
    }

    @Test
    @DisplayName("게시글 좋아요 성공: 처음 누른 좋아요면 201과 성공 메시지를 반환한다")
    void likePost_success() throws Exception {
        doNothing().when(postService).likePost(POST_ID, MEMBER_ID);

        mockMvc.perform(post("/posts/{postId}/like", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("좋아요를 눌렀습니다."));

        verify(postService).likePost(POST_ID, MEMBER_ID);
    }

    @Test
    @DisplayName("게시글 좋아요 실패: 이미 좋아요한 게시글이면 400을 반환한다")
    void likePost_fail_alreadyExists() throws Exception {
        doThrow(new CustomException(ErrorCode.POST_LIKE_ALREADY_EXISTS))
                .when(postService).likePost(POST_ID, MEMBER_ID);

        mockMvc.perform(post("/posts/{postId}/like", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("POST_LIKE_ALREADY_EXISTS"));

        verify(postService).likePost(POST_ID, MEMBER_ID);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 성공: 좋아요가 있으면 204를 반환한다")
    void unlikePost_success() throws Exception {
        doNothing().when(postService).unlikePost(POST_ID, MEMBER_ID);

        mockMvc.perform(delete("/posts/{postId}/like", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(postService).unlikePost(POST_ID, MEMBER_ID);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 실패: 좋아요가 없으면 404를 반환한다")
    void unlikePost_fail_notFound() throws Exception {
        doThrow(new CustomException(ErrorCode.POST_LIKE_NOT_FOUND))
                .when(postService).unlikePost(POST_ID, MEMBER_ID);

        mockMvc.perform(delete("/posts/{postId}/like", POST_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value("POST_LIKE_NOT_FOUND"));

        verify(postService).unlikePost(POST_ID, MEMBER_ID);
    }

    private PostResponseDto postResponse(String title, String content) {
        return new PostResponseDto(
                POST_ID,
                BOARD_ID,
                MEMBER_ID,
                title,
                false,
                content,
                3L,
                2L,
                CREATED_AT,
                MODIFIED_AT
        );
    }

    private RequestPostProcessor authenticatedMember(Long memberId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        new AuthenticatedMember(memberId),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        return authentication(authenticationToken);
    }
}
