package com.example.community.post.controller;

import com.example.community.post.dto.request.CreatePostRequest;
import com.example.community.post.dto.response.PostResponse;
import com.example.community.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("게시글 생성")
    void create_post() throws Exception {
        // given
        Long boardId = 1L;
        Long memberId = 1L;

        CreatePostRequest request = new CreatePostRequest(true, "제목", "게시글 내용");
        PostResponse response = new PostResponse(1L,
                "제목",
                "게시글 내용",
                true,
                boardId,
                "게시판 제목",
                memberId,
                "닉네임",
                LocalDateTime.now(),
                LocalDateTime.now());

        given(postService.createPost(eq(boardId), eq(memberId), any(CreatePostRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/boards/1/posts")
                        .header("Auth-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("게시글 내용"));
    }
}