package efub.assignment.community.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.dto.request.ProfileUpdateRequestDto;
import efub.assignment.community.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class MemberControllerTest {

    // 의존성
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    private Member testMember;

    // 테스트용 DB 삽입
    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@test.com")
                .password("password")
                .nickname("nickname")
                .build();

        memberRepository.save(testMember);
    }

    @Test
    @DisplayName("GET /members/{memberId} -> 200 & 응답 필드 검증")
    void getMember_200() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{memberId}", testMember.getMemberId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @Test
    @DisplayName("GET /members/{memberId_not_exist} -> exception 발생")
    void getMember_memberNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/members/{memberId}", 9999999L))
                .andExpect(status().isNotFound())
                .andExpect((result -> {
                    Exception e = result.getResolvedException();
                    assertThat(e).isInstanceOf(CustomException.class);
                    assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_NOT_FOUND);
                }));
    }

    @Test
    @DisplayName("POST /members -> 201 & 응답 필드 검증 & H2 저장")
    void postMember_201() throws Exception {
        // given
        String body = """
                {"email":"newtest@test.com", "password":"password", "nickname":"name", "university":"이화여대", "studentId":"1111111"}
                """;

        // when
        MvcResult res = mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("^/members/\\d+$")))
                .andReturn();

        // then
        String location = res.getResponse().getHeader("Location");
        long id = Long.parseLong(java.net.URI.create(location).getPath().replace("/members/", ""));

        em.flush();
        em.clear();

        Member savedMember = memberRepository.findByMemberId(id).orElseThrow();
        assertThat(savedMember.getEmail()).isEqualTo("newtest@test.com");
        assertThat(savedMember.getNickname()).isEqualTo("name");
    }

    @Test
    @DisplayName("POST /members 중복 이메일 -> exception 발생 & DB 저장 x")
    void postMember_duplicateEmail() throws Exception {
        // given
        long before = memberRepository.count();

        String body = """
                {"email":"test@test.com", "password":"password", "nickname":"닉네임", "university":"이화여대", "studentId":"3333333"}
                """;

        // when & then
        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    Exception e = result.getResolvedException();
                    assertThat(e).isInstanceOf(CustomException.class);
                    assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_EMAIL);
                });

        assertThat(memberRepository.count()).isEqualTo(before);
    }

    @Test
    @DisplayName("PATCH /members/profile/{memberId} -> 200 & 응답 필드 검증")
    void patchMemberProfile_200() throws Exception {
        // given
        Long id = testMember.getMemberId();
        String updatedNickname = "수정닉네임";

        ProfileUpdateRequestDto request = ProfileUpdateRequestDto.builder()
                .nickname(updatedNickname).build();

        // when
        mockMvc.perform(patch("/members/profile/{memberId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(updatedNickname));

        // then
        em.flush();
        em.clear();  // 1차 캐시 비우기

        Member foundMember = memberRepository.findByMemberId(id).orElseThrow();
        assertThat(foundMember.getNickname()).isEqualTo(updatedNickname);
    }

    @Test
    @DisplayName("PATCH /members/profile/{memberId_not_exist} -> exception 발생")
    void patchMemberProfile_memberNotFound() throws Exception {
        // given
        ProfileUpdateRequestDto request = ProfileUpdateRequestDto.builder()
                .nickname("수정닉네임").build();

        // when & then
        mockMvc.perform(patch("/members/profile/{memberId}", 9999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Exception e = result.getResolvedException();
                    assertThat(e).isInstanceOf(CustomException.class);
                    assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("PATCH /members/profile/{memberId} 잘못된 요청 필드 -> exception 발생")
    void patchMemberProfile_invalidRequest() throws Exception {
        // given
        ProfileUpdateRequestDto request = ProfileUpdateRequestDto.builder()
                .nickname("").build();

        // when & then
        mockMvc.perform(patch("/members/profile/{memberId}", testMember.getMemberId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MethodArgumentNotValidException.class));
    }

    @Test
    @DisplayName("DELETE /members/{memberId} -> 200 & 응답 필드 검증")
    void deleteMember_200() throws Exception {
        // given
        Long id = testMember.getMemberId();

        mockMvc.perform(delete("/members/{memberId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 탈퇴되었습니다."));

        // then
        em.flush();
        em.clear();

        assertThat(memberRepository.findByMemberId(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /members/{memberId_not_exist} -> exception 발생")
    void deleteMember_memberNotFound() throws Exception {
        mockMvc.perform(delete("/members/{memberId}", 9999999L))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Exception e = result.getResolvedException();
                    assertThat(e).isInstanceOf(CustomException.class);
                    assertThat(((CustomException) e).getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_NOT_FOUND);
                });
    }
}
