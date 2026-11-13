package efub.assignment.community.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .email("efub@test.com")
                .password("password")
                .nickname("김이화")
                .school("이화여대")
                .studentId("2020000000")
                .build());
    }


    @Test
    @DisplayName("회원 생성 성공: 201 Created, 응답 body를 반환하고 DB에 저장된다")
    void createMember_success() throws Exception {
        // given
        Map<String, Object> request = Map.of(
                "email", "new@test.com",
                "password", "password",
                "nickname", "새이화",
                "school", "이화여대",
                "studentId", "2021000000"
        );

        // when
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.nickname").value("새이화"))
                .andExpect(jsonPath("$.school").value("이화여대"))
                .andExpect(jsonPath("$.studentId").value("2021000000"));

        Member saved = memberRepository.findByEmail("new@test.com").orElseThrow();
        assertThat(saved.getNickname()).isEqualTo("새이화");
        assertThat(memberRepository.count()).isEqualTo(2L); // setUp의 회원 + 새 회원
    }

    @Test
    @DisplayName("회원 생성 실패: 이메일이 비어있으면 400 Bad Request, DB에 저장되지 않는다")
    void createMember_fail_blankEmail() throws Exception {
        // given
        Map<String, Object> request = Map.of(
                "email", "",
                "password", "password",
                "nickname", "새이화",
                "school", "이화여대",
                "studentId", "2021000000"
        );

        // when
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));

        assertThat(memberRepository.count()).isEqualTo(1L); // setUp의 회원만 존재
    }


    @Test
    @DisplayName("회원 단건 조회 성공: 200 OK, 회원 정보를 반환한다")
    void getMember_success() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{memberId}", member.getMemberId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getMemberId()))
                .andExpect(jsonPath("$.email").value("efub@test.com"))
                .andExpect(jsonPath("$.nickname").value("김이화"))
                .andExpect(jsonPath("$.school").value("이화여대"))
                .andExpect(jsonPath("$.studentId").value("2020000000"));
    }

    @Test
    @DisplayName("회원 단건 조회 실패: 존재하지 않는 회원이면 404 Not Found")
    void getMember_fail_memberNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{memberId}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"));
    }


    @Test
    @DisplayName("닉네임 수정 성공: 200 OK, 응답과 DB의 닉네임이 변경된다")
    void updateMember_success() throws Exception {
        // given
        Map<String, Object> request = Map.of("nickname", "바뀐이화");

        // when
        mockMvc.perform(patch("/members/profile/{memberId}", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getMemberId()))
                .andExpect(jsonPath("$.nickname").value("바뀐이화"));

        Member updated = memberRepository.findById(member.getMemberId()).orElseThrow();
        assertThat(updated.getNickname()).isEqualTo("바뀐이화");
    }

    @Test
    @DisplayName("닉네임 수정 실패: 닉네임이 비어있으면 400 Bad Request, 닉네임이 변경되지 않는다")
    void updateMember_fail_blankNickname() throws Exception {
        // given
        Map<String, Object> request = Map.of("nickname", "");

        // when
        mockMvc.perform(patch("/members/profile/{memberId}", member.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));

        Member notUpdated = memberRepository.findById(member.getMemberId()).orElseThrow();
        assertThat(notUpdated.getNickname()).isEqualTo("김이화");
    }


    @Test
    @DisplayName("회원 탈퇴 성공: 200 OK, 메시지를 반환하고 DB의 상태가 UNREGISTER로 변경된다")
    void deleteMember_success() throws Exception {
        // when
        mockMvc.perform(patch("/members/{memberId}", member.getMemberId()))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string("message: 성공적으로 탈퇴되었습니다."));

        Member unregistered = memberRepository.findById(member.getMemberId()).orElseThrow();
        assertThat(unregistered.getStatus()).isEqualTo(MemberStatus.UNREGISTER);
    }

    @Test
    @DisplayName("회원 탈퇴 실패: 존재하지 않는 회원이면 404 Not Found")
    void deleteMember_fail_memberNotFound() throws Exception {
        // when & then
        mockMvc.perform(patch("/members/{memberId}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"));
    }
}
