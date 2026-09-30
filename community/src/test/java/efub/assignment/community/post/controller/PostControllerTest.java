package efub.assignment.community.post.controller;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repositoriy.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.repositoriy.PostRepository;
import efub.assignment.community.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;
    private Member author;
    private Board board;
    PostCreateRequestDto requestDto;
    Post savedPost;
    PostResponseDto responseDto;
    Long postId;
    Long authorId;
    Long boardId;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        author = Member.builder()
                .email("test@example.com")
                .password("password")
                .nickname("회원1")
                .studentId("1234")
                .university("이화여자대학교")
                .build();
        author = memberRepository.save(author);

        board = Board.builder()
                .title("게시판1")
                .boardOwner(author)
                .description("게시판 설명")
                .build();
        board = boardRepository.save(board);

        authorId = author.getMemberId();
        boardId = board.getBoardId();

        requestDto = new PostCreateRequestDto(boardId, authorId, "제목", "게시글 내용입니다");
    }

    @Test
    @DisplayName("성공 - 게시글 생성 (Controller)")
    void create_post() throws Exception {

        //when & then
        MvcResult result = mockMvc.perform(post("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("^/posts/\\d+$")))
                .andExpect(jsonPath("$.boardId").value(boardId))
                .andExpect(jsonPath("$.accountId").value(authorId))
                .andExpect(jsonPath("$.nickName").value("회원1"))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("게시글 내용입니다"))
                .andExpect(jsonPath("$.viewCount").value(0L))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        long id = Long.parseLong(URI.create(location).getPath().replace("/posts/",""));

        assertTrue(postRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("실패 - 내용이 5자 미만")
    void create_post_contentTooShort_fail() throws Exception {
        PostCreateRequestDto request = new PostCreateRequestDto(boardId, authorId, "제목", "내용");

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("GET /post/{id} -> 200 응답 & 응답 필드 검증")
    void getPost_200() throws Exception {
        Post post = Post.builder()
                .title("제목")
                .content("내용내용내용")
                .writer(author)
                .board(board)
                .build();
        postRepository.save(post);

        //when & then
        mockMvc.perform(get("/posts/{id}", post.getPostId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용내용내용"));
    }

}
