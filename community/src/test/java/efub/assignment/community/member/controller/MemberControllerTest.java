package efub.assignment.community.member.controller;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberControllerTest {

    // 의존성 주입
    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;

    Member member;

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
    }


    @Test
    @DisplayName("회원 생성 성공")
    void create_member_success() throws Exception {
        // given
        String body = """
                {
                    "studentId": 2371002,
                    "university": "이화여자대학교",
                    "nickname": "테스트회원",
                    "email": "test@example.com",
                    "password": "password"
                }
                """;

        // when & then
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(2371002))
                .andExpect(jsonPath("$.university").value("이화여자대학교"))
                .andExpect(jsonPath("$.nickname").value("테스트회원"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        // H2 DB 실제 저장 여부 확인
        assertTrue(memberRepository.existsByEmail("test@example.com"));
        assertTrue(memberRepository.existsByStudentId(2371002L));
    }


    @Test
    @DisplayName("필수값이 누락되면 회원 생성에 실패한다")
    void create_member_fail_when_required_value_is_missing() throws Exception {
        // given
        String body = """
                {
                    "studentId": 2371002,
                    "university": "이화여자대학교",
                    "nickname": "테스트회원",
                    "email": "test@example.com"
                }
                """;

        // when & then
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        assertFalse(memberRepository.existsByEmail("test@example.com"));
    }


    @Test
    @DisplayName("회원 조회 성공")
    void get_member_success() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{memberId}", member.getMemberId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.studentId").value(2371001))
                .andExpect(jsonPath("$.university").value("이화여자대학교"))
                .andExpect(jsonPath("$.nickname").value("김이화"))
                .andExpect(jsonPath("$.email").value("ewha@example.com"));
    }


    @Test
    @DisplayName("존재하지 않는 회원 조회 실패")
    void get_member_fail_when_member_not_found() throws Exception {
        // given
        Long invalidMemberId = 999L;

        // when & then
        mockMvc.perform(get("/members/{memberId}", invalidMemberId))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("회원 수정 성공")
    void update_member_success() throws Exception {
        // given
        String body = """
                {
                    "nickname": "수정된닉네임",
                    "email": "updated@example.com",
                    "password": "newPassword"
                }
                """;

        // when & then
        mockMvc.perform(patch("/members/{memberId}", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.nickname").value("수정된닉네임"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        // H2 DB 실제 수정 여부 확인
        Member updatedMember = memberRepository
                .findByMemberId(member.getMemberId())
                .orElseThrow();

        assertEquals("수정된닉네임", updatedMember.getNickname());
        assertEquals("updated@example.com", updatedMember.getEmail());
        assertEquals("newPassword", updatedMember.getPassword());
    }


    @Test
    @DisplayName("비밀번호가 없으면 회원 수정에 실패한다")
    void update_member_fail_when_password_is_missing() throws Exception {
        // given
        String body = """
                {
                    "nickname": "수정된닉네임",
                    "email": "updated@example.com"
                }
                """;

        // when & then
        mockMvc.perform(patch("/members/{memberId}", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        // 기존 데이터가 변경되지 않았는지 확인
        Member savedMember = memberRepository
                .findByMemberId(member.getMemberId())
                .orElseThrow();

        assertEquals("김이화", savedMember.getNickname());
        assertEquals("ewha@example.com", savedMember.getEmail());
        assertEquals("password", savedMember.getPassword());
    }


    @Test
    @DisplayName("회원 삭제 성공")
    void delete_member_success() throws Exception {
        // given
        Long memberId = member.getMemberId();

        // when & then
        mockMvc.perform(delete("/members/{memberId}", memberId))
                .andExpect(status().isNoContent());

        // H2 DB 실제 삭제 여부 확인
        assertFalse(memberRepository.existsById(memberId));
    }


    @Test
    @DisplayName("존재하지 않는 회원 삭제 실패")
    void delete_member_fail_when_member_not_found() throws Exception {
        // given
        Long invalidMemberId = 999L;

        // when & then
        mockMvc.perform(delete("/members/{memberId}", invalidMemberId))
                .andExpect(status().isNotFound());
    }
}