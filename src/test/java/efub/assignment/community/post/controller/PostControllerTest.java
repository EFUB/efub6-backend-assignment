package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.PostLike;
import efub.assignment.community.post.repository.PostLikeRepository;
import efub.assignment.community.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화: 순수 통합 테스트만 진행
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;

    private Member member;      // 게시글 작성자
    private Member otherMember; // 작성자가 아닌 다른 회원
    private Board board;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .email("efub@test.com")
                .password("password")
                .nickname("김이화")
                .school("이화여대")
                .studentId("2020000000")
                .build());

        otherMember = memberRepository.save(Member.builder()
                .email("other@test.com")
                .password("password")
                .nickname("박이화")
                .school("이화여대")
                .studentId("2020000001")
                .build());

        board = boardRepository.save(Board.builder()
                .writer(member)
                .name("자유게시판")
                .description("자유롭게 이야기하는 게시판")
                .build());
    }

    private Post savePost(String title, String content) {
        return postRepository.save(Post.builder()
                .board(board)
                .writer(member)
                .title(title)
                .content(content)
                .isAnonymous(false)
                .build());
    }


    @Test
    @DisplayName("게시글 생성 성공: 201 Created, Location 헤더, DB에 저장된다")
    void createPost_success() throws Exception {
        // given
        Map<String, Object> request = Map.of(
                "title", "테스트 제목",
                "content", "테스트 내용입니다.",
                "isAnonymous", false
        );

        // when
        mockMvc.perform(post("/boards/{boardId}/posts", board.getId())
                        .header("Auth-Id", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.matchesPattern("/boards/" + board.getId() + "/posts/\\d+")));

        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(1);
        Post saved = posts.get(0);
        assertThat(saved.getTitle()).isEqualTo("테스트 제목");
        assertThat(saved.getContent()).isEqualTo("테스트 내용입니다.");
        assertThat(saved.isAnonymous()).isFalse();
        assertThat(saved.getWriter().getMemberId()).isEqualTo(member.getMemberId());
    }

    @Test
    @DisplayName("게시글 생성 실패: 제목이 비어있으면 400 Bad Request, DB에 저장되지 않는다")
    void createPost_fail_blankTitle() throws Exception {
        // given
        Map<String, Object> request = Map.of(
                "title", "",
                "content", "테스트 내용입니다.",
                "isAnonymous", false
        );

        // when
        mockMvc.perform(post("/boards/{boardId}/posts", board.getId())
                        .header("Auth-Id", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));

        assertThat(postRepository.count()).isZero();
    }


    @Test
    @DisplayName("게시글 전체 조회 성공: 200 OK, 저장된 게시글 목록과 총 개수를 반환한다")
    void getAllPosts_success() throws Exception {
        // given
        savePost("첫 번째 제목", "첫 번째 내용입니다.");
        savePost("두 번째 제목", "두 번째 내용입니다.");

        // when & then
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPosts").value(2))
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[*].title",
                        org.hamcrest.Matchers.containsInAnyOrder("첫 번째 제목", "두 번째 제목")));
    }

    @Test
    @DisplayName("게시글 전체 조회 성공: 게시글이 없으면 200 OK, 빈 목록을 반환한다")
    void getAllPosts_success_empty() throws Exception {
        // when & then
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPosts").value(0))
                .andExpect(jsonPath("$.posts").isEmpty());
    }


    @Test
    @DisplayName("게시글 단건 조회 성공: 200 OK, 게시글 내용을 반환하고 조회수가 1 증가한다")
    void getPost_success() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "테스트 내용입니다.");

        // when & then
        mockMvc.perform(get("/posts/{postId}", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(savedPost.getId()))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용입니다."))
                .andExpect(jsonPath("$.nickName").value("김이화"))
                .andExpect(jsonPath("$.viewCount").value(1));

        assertThat(postRepository.findById(savedPost.getId()).orElseThrow().getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글 단건 조회 실패: 존재하지 않는 게시글이면 404 Not Found")
    void getPost_fail_postNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/posts/{postId}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }


    @Test
    @DisplayName("게시글 수정 성공: 작성자가 수정하면 204 No Content, DB의 내용이 변경된다")
    void updatePostContent_success() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "수정 전 내용입니다.");
        Map<String, Object> request = Map.of("content", "수정 후 내용입니다.");

        // when
        mockMvc.perform(patch("/posts/{postId}", savedPost.getId())
                        .header("Auth-Id", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isNoContent());

        Post updated = postRepository.findById(savedPost.getId()).orElseThrow();
        assertThat(updated.getContent()).isEqualTo("수정 후 내용입니다.");
    }

    @Test
    @DisplayName("게시글 수정 실패: 작성자가 아니면 401 Unauthorized, 내용이 변경되지 않는다")
    void updatePostContent_fail_notWriter() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "수정 전 내용입니다.");
        Map<String, Object> request = Map.of("content", "수정 후 내용입니다.");

        // when
        mockMvc.perform(patch("/posts/{postId}", savedPost.getId())
                        .header("Auth-Id", otherMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("POST_ACCOUNT_MISMATCH"));

        Post notUpdated = postRepository.findById(savedPost.getId()).orElseThrow();
        assertThat(notUpdated.getContent()).isEqualTo("수정 전 내용입니다.");
    }


    @Test
    @DisplayName("게시글 삭제 성공: 작성자가 삭제하면 204 No Content, DB에서 삭제된다")
    void deletePost_success() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "테스트 내용입니다.");

        // when
        mockMvc.perform(delete("/posts/{postId}", savedPost.getId())
                        .header("Auth-Id", member.getMemberId()))
                // then
                .andExpect(status().isNoContent());

        assertThat(postRepository.existsById(savedPost.getId())).isFalse();
    }

    @Test
    @DisplayName("게시글 삭제 실패: 작성자가 아니면 401 Unauthorized, DB에서 삭제되지 않는다")
    void deletePost_fail_notWriter() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "테스트 내용입니다.");

        // when
        mockMvc.perform(delete("/posts/{postId}", savedPost.getId())
                        .header("Auth-Id", otherMember.getMemberId()))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("POST_ACCOUNT_MISMATCH"));

        assertThat(postRepository.existsById(savedPost.getId())).isTrue();
    }


    @Test
    @DisplayName("좋아요 생성 성공: 201 Created, 좋아요 수가 1 증가하고 DB에 저장된다")
    void likePost_success() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "테스트 내용입니다.");

        // when
        mockMvc.perform(post("/posts/{postId}/like", savedPost.getId())
                        .header("Auth-Id", otherMember.getMemberId()))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(savedPost.getId()))
                .andExpect(jsonPath("$.likeCount").value(1));

        assertThat(postLikeRepository.count()).isEqualTo(1L);
        assertThat(postRepository.findById(savedPost.getId()).orElseThrow().getLikeCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("좋아요 생성 실패: 이미 좋아요한 게시글이면 400 Bad Request, 좋아요 수가 늘지 않는다")
    void likePost_fail_alreadyLiked() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "테스트 내용입니다.");
        postLikeRepository.save(PostLike.builder().post(savedPost).member(otherMember).build());

        // when
        mockMvc.perform(post("/posts/{postId}/like", savedPost.getId())
                        .header("Auth-Id", otherMember.getMemberId()))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("POST_LIKE_ALREADY_EXISTS"));

        assertThat(postLikeRepository.count()).isEqualTo(1L);
    }


    @Test
    @DisplayName("좋아요 삭제 성공: 204 No Content, 좋아요 수가 1 감소하고 DB에서 삭제된다")
    void unlikePost_success() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "테스트 내용입니다.");
        postLikeRepository.save(PostLike.builder().post(savedPost).member(otherMember).build());
        savedPost.increaseLikeCount();
        postRepository.save(savedPost);

        // when
        mockMvc.perform(delete("/posts/{postId}/like", savedPost.getId())
                        .header("Auth-Id", otherMember.getMemberId()))
                // then
                .andExpect(status().isNoContent());

        assertThat(postLikeRepository.count()).isZero();
        assertThat(postRepository.findById(savedPost.getId()).orElseThrow().getLikeCount()).isZero();
    }

    @Test
    @DisplayName("좋아요 삭제 실패: 좋아요한 적이 없으면 404 Not Found")
    void unlikePost_fail_likeNotFound() throws Exception {
        // given
        Post savedPost = savePost("테스트 제목", "테스트 내용입니다.");

        // when & then
        mockMvc.perform(delete("/posts/{postId}/like", savedPost.getId())
                        .header("Auth-Id", otherMember.getMemberId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_LIKE_NOT_FOUND"));
    }
}
