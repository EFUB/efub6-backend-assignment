package efub.assignment.community.post.controller;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    Member member;
    Member otherMember;
    Board board;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .studentId(2371001L)
                .university("이화여자대학교")
                .nickname("김이화")
                .email("ewha@example.com")
                .password("password")
                .build();

        memberRepository.save(member);

        otherMember = Member.builder()
                .studentId(2371002L)
                .university("이화여자대학교")
                .nickname("박이화")
                .email("other@example.com")
                .password("password")
                .build();

        memberRepository.save(otherMember);

        board = Board.builder()
                .boardName("자유게시판")
                .description("자유롭게 이야기하는 게시판")
                .notice("공지사항")
                .owner(member)
                .build();

        boardRepository.save(board);
    }


    @Test
    @DisplayName("게시글 작성 성공")
    void create_post_success() throws Exception {
        // given
        String body = """
                {
                    "isAnonymous": false,
                    "content": "테스트 게시글 내용입니다."
                }
                """;

        // when & then
        mockMvc.perform(post("/boards/{boardId}/posts", board.getBoardId())
                        .header("Auth-id", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        matchesPattern("^/posts/\\d+$")
                ))
                .andExpect(jsonPath("$.content").value("테스트 게시글 내용입니다."))
                .andExpect(jsonPath("$.author.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.board.boardId").value(board.getBoardId()));

        assertEquals(1, postRepository.count());
    }


    @Test
    @DisplayName("게시글 내용이 5자 미만이면 작성에 실패한다")
    void create_post_fail_when_content_is_too_short() throws Exception {
        // given
        String body = """
                {
                    "isAnonymous": false,
                    "content": "1234"
                }
                """;

        // when & then
        mockMvc.perform(post("/boards/{boardId}/posts", board.getBoardId())
                        .header("Auth-id", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        assertEquals(0, postRepository.count());
    }


    @Test
    @DisplayName("게시글 목록 조회 성공")
    void get_all_posts_success() throws Exception {
        // given
        savePost("첫 번째 게시글입니다.", member);
        savePost("두 번째 게시글입니다.", member);

        // when & then
        mockMvc.perform(get("/boards/{boardId}/posts", board.getBoardId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(2)));
    }


    @Test
    @DisplayName("존재하지 않는 게시판의 게시글 목록 조회 실패")
    void get_all_posts_fail_when_board_not_found() throws Exception {
        // given
        Long invalidBoardId = 999L;

        // when & then
        mockMvc.perform(get("/boards/{boardId}/posts", invalidBoardId))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("게시글 상세 조회 성공")
    void get_post_success() throws Exception {
        // given
        Post post = savePost("조회할 게시글 내용입니다.", member);

        // when & then
        mockMvc.perform(get("/posts/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.content").value("조회할 게시글 내용입니다."))
                .andExpect(jsonPath("$.author.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.board.boardId").value(board.getBoardId()));
    }


    @Test
    @DisplayName("존재하지 않는 게시글 조회 실패")
    void get_post_fail_when_post_not_found() throws Exception {
        // given
        Long invalidPostId = 999L;

        // when & then
        mockMvc.perform(get("/posts/{postId}", invalidPostId))
                .andExpect(status().isNotFound());
    }


    // 지난주 테스트 내용 유지
    @Test
    @DisplayName("게시글 수정 API 테스트")
    void update_post() throws Exception {
        // given
        Post post = savePost("수정 전 게시글 내용입니다.", member);

        String body = """
                {
                    "content": "수정된 게시글 내용입니다."
                }
                """;

        // when & then
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .header("Auth-id", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.content").value("수정된 게시글 내용입니다."))
                .andExpect(jsonPath("$.author.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.author.authorNickname").value("김이화"))
                .andExpect(jsonPath("$.author.isAnonymous").value(false))
                .andExpect(jsonPath("$.board.boardName").value("자유게시판"));

        Post updatedPost = postRepository
                .findById(post.getId())
                .orElseThrow();

        assertEquals("수정된 게시글 내용입니다.", updatedPost.getContent());
    }


    // 지난주 테스트 내용 유지
    @Test
    @DisplayName("게시글 작성자가 아니면 수정 요청에 실패한다.")
    void update_post_fail_when_not_author() throws Exception {
        // given
        Post post = savePost("원래 게시글 내용입니다.", member);

        String body = """
                {
                    "content": "수정된 게시글 내용입니다."
                }
                """;

        // when & then
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .header("Auth-id", otherMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        Post savedPost = postRepository
                .findById(post.getId())
                .orElseThrow();

        assertEquals("원래 게시글 내용입니다.", savedPost.getContent());
    }


    // 지난주 테스트 내용 유지
    @ParameterizedTest(name = "{index}번째 반복-게시글 내용: \"{0}\" => 수정 요청 실패")
    @ValueSource(strings = {"", "1", "1234"})
    @DisplayName("게시글 내용이 5자 미만이면 수정 요청에 실패한다.")
    void update_post_fail_when_content_is_too_short(String content) throws Exception {
        // given
        Post post = savePost("원래 게시글 내용입니다.", member);

        String body = """
                {
                    "content": "%s"
                }
                """.formatted(content);

        // when & then
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .header("Auth-id", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        Post savedPost = postRepository
                .findById(post.getId())
                .orElseThrow();

        assertEquals("원래 게시글 내용입니다.", savedPost.getContent());
    }


    @Test
    @DisplayName("게시글 삭제 성공")
    void delete_post_success() throws Exception {
        // given
        Post post = savePost("삭제할 게시글 내용입니다.", member);
        Long postId = post.getId();

        // when & then
        mockMvc.perform(delete("/posts/{postId}", postId)
                        .header("Auth-id", member.getMemberId()))
                .andExpect(status().isNoContent());

        assertFalse(postRepository.existsById(postId));
    }


    @Test
    @DisplayName("게시글 작성자가 아니면 삭제에 실패한다")
    void delete_post_fail_when_not_author() throws Exception {
        // given
        Post post = savePost("삭제할 수 없는 게시글입니다.", member);

        // when & then
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .header("Auth-id", otherMember.getMemberId()))
                .andExpect(status().isUnauthorized());

        assertTrue(postRepository.existsById(post.getId()));
    }


    @Test
    @DisplayName("게시글 좋아요 성공")
    void like_post_success() throws Exception {
        // given
        Post post = savePost("좋아요 테스트 게시글입니다.", member);

        // when & then
        mockMvc.perform(post("/posts/{postId}/likes", post.getId())
                        .header("Auth-id", member.getMemberId()))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/posts/" + post.getId() + "/likes"
                ))
                .andExpect(jsonPath("$.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.postId").value(post.getId()));

        assertTrue(postLikeRepository.existsByPostAndMember(post, member));
    }


    @Test
    @DisplayName("이미 좋아요한 게시글에 다시 좋아요하면 실패한다")
    void like_post_fail_when_like_already_exists() throws Exception {
        // given
        Post post = savePost("좋아요 중복 테스트 게시글입니다.", member);

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();

        postLikeRepository.save(postLike);

        // when & then
        mockMvc.perform(post("/posts/{postId}/likes", post.getId())
                        .header("Auth-id", member.getMemberId()))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("게시글 좋아요 취소 성공")
    void unlike_post_success() throws Exception {
        // given
        Post post = savePost("좋아요 취소 테스트 게시글입니다.", member);

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();

        postLikeRepository.save(postLike);

        // when & then
        mockMvc.perform(delete("/posts/{postId}/likes", post.getId())
                        .header("Auth-id", member.getMemberId()))
                .andExpect(status().isNoContent());

        assertFalse(postLikeRepository.existsByPostAndMember(post, member));
    }


    @Test
    @DisplayName("존재하지 않는 좋아요를 취소하면 실패한다")
    void unlike_post_fail_when_like_not_found() throws Exception {
        // given
        Post post = savePost("좋아요가 없는 게시글입니다.", member);

        // when & then
        mockMvc.perform(delete("/posts/{postId}/likes", post.getId())
                        .header("Auth-id", member.getMemberId()))
                .andExpect(status().isNotFound());
    }


    private Post savePost(String content, Member author) {
        Post post = Post.builder()
                .author(author)
                .board(board)
                .isAnonymous(false)
                .content(content)
                .build();

        return postRepository.save(post);
    }
}