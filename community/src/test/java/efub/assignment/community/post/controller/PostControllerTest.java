package efub.assignment.community.post.controller;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PostController.class,
        excludeAutoConfiguration = {
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
@MockitoBean(types = JpaMetamodelMappingContext.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PostService postService;
    private Member author;
    private Board board;
    PostCreateRequestDto requestDto;
    Post savedPost;
    PostResponseDto responseDto;
    Long postId;
    Long authorId;
    Long boardId;

    @BeforeEach
    void setUp() {
        boardId = 1L;
        authorId = 2L;
        postId = 3L;

        author = Member.builder()
                .email("test@example.com")
                .password("password")
                .nickname("회원1")
                .studentId("1234")
                .university("이화여자대학교")
                .build();
        ReflectionTestUtils.setField(author, "memberId", authorId);

        board = Board.builder()
                .title("게시판1")
                .boardOwner(author)
                .description("게시판 설명")
                .build();
        ReflectionTestUtils.setField(board, "boardId", boardId);

        requestDto = new PostCreateRequestDto(boardId, authorId, "제목", "게시글 내용입니다");
        savedPost = requestDto.toEntity(board, author);
        ReflectionTestUtils.setField(savedPost, "postId", postId);
        responseDto = PostResponseDto.from(savedPost);
    }

    @Test
    @DisplayName("성공 - 게시글 생성 (Controller)")
    void create_post() throws Exception {
        //given
        given(postService.createPost(any(PostCreateRequestDto.class))).willReturn(responseDto);

        //when & then
        mockMvc.perform(post("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.boardId").value(boardId))
                .andExpect(jsonPath("$.accountId").value(authorId))
                .andExpect(jsonPath("$.nickName").value("회원1"))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("게시글 내용입니다"))
                .andExpect(jsonPath("$.viewCount").value(0L));

        verify(postService).createPost(any(PostCreateRequestDto.class));

    }

    @Test
    @DisplayName("실패 - 내용이 5자 미만")
    void create_post_contentTooShort_fail() throws Exception {
        PostCreateRequestDto request = new PostCreateRequestDto(boardId, authorId, "제목", "내용");

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(postService, never()).createPost(any());
    }

}
