package com.example.community.post.controller;

import com.example.community.post.dto.request.CreatePostRequest;
import com.example.community.post.dto.request.UpdatePostRequest;
import com.example.community.post.dto.response.PostListResponse;
import com.example.community.post.dto.response.PostResponse;
import com.example.community.post.dto.summary.PostSummary;
import com.example.community.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles(profiles = "test")
@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PostService postService;

    Long boardId = 1L;
    Long memberId = 1L;


    @Nested
    @DisplayName("POST /boards/{boardId}/posts - 글 생성")
    class CreatePost {
        @Test
        @DisplayName("유효한 글 작성 요청이면 게시글을 생성하고 201 Created를 반환한다")
        void createPost_whenRequestIsValid_returnsCreated() throws Exception {
            // given
            CreatePostRequest request = new CreatePostRequest(
                    false, "제목", "내용내용내용"
            );
            PostResponse response = new PostResponse(
                    1L, "제목", "내용내용내용", false,
                    boardId, "게시판", memberId, "닉네임",
                    LocalDateTime.now(), LocalDateTime.now()
            );
            given(postService.createPost(boardId, memberId, request)).willReturn(response);

            // when & then
            mockMvc.perform(post("/boards/{boardId}/posts", boardId)
                            .header("Auth-Id", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value(request.title()))
                    .andExpect(jsonPath("$.boardId").value(boardId))
                    .andExpect(jsonPath("$.memberId").value(memberId));
            verify(postService).createPost(boardId, memberId, request);
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidCreatePostRequests")
        @DisplayName("유효하지 않은 글 작성 요청이면 400 Bad Request를 반환한다")
        void createPost_whenRequestIsInvalid_returnsBadRequest(
                String description,
                CreatePostRequest request
        ) throws Exception {
            // when & then
            mockMvc.perform(post("/boards/{boardId}/posts", boardId)
                        .header("Auth-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(postService);
        }

        static Stream<Arguments> invalidCreatePostRequests() {
            return Stream.of(
                    Arguments.of(
                            "익명 여부가 null인 경우",
                            new CreatePostRequest(
                                    null, "제목", "내용내용내용"
                            )
                    ),
                    Arguments.of(
                            "제목이 blank인 경우",
                            new CreatePostRequest(
                                    true, "   ", "내용내용내용"
                            )
                    ),
                    Arguments.of(
                            "내용이 5자 미만인 경우",
                            new CreatePostRequest(
                                    true, "제목", "내용"
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("GET /boards/{boardId}/posts - 글 목록 조회")
    class GetAllPosts {
        @Test
        @DisplayName("게시판의 게시글 목록을 조회하면 200 OK와 게시글 목록을 반환한다")
        void getAllPosts_whenBoardExists_returnsPostList() throws Exception {
            // given
            PostListResponse response = new PostListResponse(
                    List.of(new PostSummary(
                            1L, boardId, "닉네임", false,
                            "제목", "내용내용내용", LocalDateTime.now(), LocalDateTime.now()
                    )),
                    1L
            );
            given(postService.getAllPosts(boardId)).willReturn(response);

            // when & then
            mockMvc.perform(get("/boards/{boardId}/posts", boardId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPosts").value(1L))
                    .andExpect(jsonPath("$.posts").isArray());
            verify(postService).getAllPosts(boardId);
        }

        @Test
        @DisplayName("게시판 ID가 숫자가 아니면 400 Bad Request를 반환한다")
        void getAllPosts_whenBoardIdIsInvalid_returnsBadRequest() throws Exception {
            // when & then
            mockMvc.perform(get("/boards/{boardId}/posts", "invalid"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(postService);
        }
    }

    @Nested
    @DisplayName("GET /posts/{postId} - 글 상세 조회")
    class GetPost {
        @Test
        @DisplayName("존재하는 게시글 ID로 요청하면 게시글 정보와 200 OK를 반환한다")
        void getPost_whenPostExists_returnsPost() throws Exception {
            // given
            Long postId = 1L;
            PostResponse response = new PostResponse(
                    postId, "제목", "내용내용내용", false,
                    boardId, "게시판", memberId, "닉네임",
                    LocalDateTime.now(), LocalDateTime.now()
            );
            given(postService.getPost(postId)).willReturn(response);

            // when & then
            mockMvc.perform(get("/posts/{postId}", postId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.boardId").value(boardId))
                    .andExpect(jsonPath("$.memberId").value(memberId))
                    .andExpect(jsonPath("$.postId").value(postId));
            verify(postService).getPost(postId);
        }

        @Test
        @DisplayName("게시글 ID가 숫자가 아니면 400 Bad Request를 반환한다")
        void getPost_whenPostIdIsInvalid_returnsBadRequest() throws Exception {
            // when & then
            mockMvc.perform(get("/posts/{postId}", "invalid"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(postService);
        }
    }

    @Nested
    @DisplayName("PATCH /posts/{postId} - 글 수정")
    class UpdatePostContent {
        @Test
        @DisplayName("유효한 수정 요청이면 게시글을 수정하고 204 No Content를 반환한다")
        void updatePostContent_whenRequestIsValid_returnsNoContent() throws Exception {
            // given
            Long postId = 1L;
            UpdatePostRequest request = new UpdatePostRequest(
                    "수정된 제목", "수정된 내용"
            );

            // when & then
            mockMvc.perform(patch("/posts/{postId}", postId)
                        .header("Auth-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
            verify(postService).updatePostContent(postId, memberId, request);
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidUpdatePostRequests")
        @DisplayName("유효하지 않은 수정 요청이면 400 Bad Request를 반환한다")
        void updatePostContent_whenRequestIsInvalid_returnsBadRequest(
                String description,
                UpdatePostRequest request
        ) throws Exception {
            // given
            Long postId = 1L;

            // when & then
            mockMvc.perform(patch("/posts/{postId}",postId)
                        .header("Auth-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(postService);
        }

        static Stream<Arguments> invalidUpdatePostRequests() {
            return Stream.of(
                    Arguments.of(
                            "수정된 내용이 5자 미만인 경우",
                            new UpdatePostRequest(
                                    "수정된 제목", "수정"
                            )
                    ),
                    Arguments.of(
                            "수정된 내용이 500자를 초과하는 경우",
                            new UpdatePostRequest(
                                    "수정된 제목", "수".repeat(501)
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("DELETE /posts/{postId} - 글 삭제")
    class DeletePost {
        @Test
        @DisplayName("게시글 삭제에 성공하면 204 No Content를 반환한다")
        void deletePost_whenPostExists_returnsNoContent() throws Exception {
            // given
            Long postId = 1L;

            // when & then
            mockMvc.perform(delete("/posts/{postId}", postId)
                        .header("Auth-Id", memberId))
                    .andExpect(status().isNoContent());
            verify(postService).deletePost(postId, memberId);
        }

        @Test
        @DisplayName("Auth-Id 헤더가 없으면 400 Bad Request를 반환한다")
        void deletePost_whenAuthIdHeaderIsMissing_returnsBadRequest() throws Exception {
            // given
            Long postId = 1L;

            // when & then
            mockMvc.perform(delete("/posts/{postId}", postId))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(postService);
        }
    }

    @Nested
    @DisplayName("POST /posts/{postId}/likes - 글 좋아요 생성")
    class CreatePostLike {
        @Test
        @DisplayName("좋아요 생성에 성공하면 201 Created와 메시지를 반환한다")
        void createPostLike_whenPostExists_returnsCreated() throws Exception {
            // given
            Long postId = 1L;

            // when & then
            mockMvc.perform(post("/posts/{postId}/likes", postId)
                    .header("Auth-Id", memberId))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").value("좋아요를 눌렀습니다"));
            verify(postService).createPostLike(postId, memberId);
        }

        @Test
        @DisplayName("Auth-Id 헤더가 없으면 400 Bad Request를 반환한다")
        void createPostLike_whenAuthIdHeaderIsMissing_returnsBadRequest() throws Exception {
            // given
            Long postId = 1L;

            // when & then
            mockMvc.perform(post("/posts/{postId}/likes", postId))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(postService);
        }
    }

    @Nested
    @DisplayName("DELETE /posts/{postId}/likes - 글 좋아요 삭제")
    class DeletePostLike {
        @Test
        @DisplayName("좋아요 삭제에 성공하면 204 No Content와 메시지를 반환한다")
        void deletePostLike_whenPostExists_returnsNoContent() throws Exception {
            // given
            Long postId = 1L;

            // when & then
            mockMvc.perform(delete("/posts/{postId}/likes", postId)
                        .header("Auth-Id", memberId))
                    .andExpect(status().isNoContent());
            verify(postService).deletePostLike(postId, memberId);
        }

        @Test
        @DisplayName("Auth-Id 헤더가 없으면 400 Bad Request를 반환한다")
        void deletePostLike_whenAuthIdHeaderIsMissing_returnsBadRequest() throws Exception {
            // given
            Long postId = 1L;

            // when & then
            mockMvc.perform(delete("/posts/{postId}/likes", postId))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(postService);
        }

    }

}
