package efub.assignment.community.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.security.AuthenticatedMember;
import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.UpdateMemberNicknameRequestDto;
import efub.assignment.community.member.dto.response.CreateMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.service.MemberService;
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

@WebMvcTest(MemberController.class)
@ActiveProfiles("test")
class MemberControllerTest {

    private static final Long MEMBER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("회원 생성 성공: 유효한 요청이면 201과 생성된 회원을 반환한다")
    void createMember_success() throws Exception {
        CreateMemberRequestDto request = createMemberRequest();
        CreateMemberResponseDto response = new CreateMemberResponseDto(
                MEMBER_ID,
                request.studentId(),
                request.university(),
                request.nickname(),
                request.email()
        );
        when(memberService.createMember(request)).thenReturn(response);

        mockMvc.perform(post("/members")
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.memberId").value(MEMBER_ID))
                .andExpect(jsonPath("$.studentId").value("20240001"))
                .andExpect(jsonPath("$.nickname").value("이펍"))
                .andExpect(jsonPath("$.email").value("member@example.com"));

        verify(memberService).createMember(request);
    }

    @Test
    @DisplayName("회원 생성 실패: 이메일 형식이 올바르지 않으면 400을 반환한다")
    void createMember_fail_invalidEmail() throws Exception {
        CreateMemberRequestDto request = new CreateMemberRequestDto(
                "20240001",
                "이화여자대학교",
                "이펍",
                "invalid-email",
                "password123!"
        );

        mockMvc.perform(post("/members")
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("email: 올바른 이메일 형식이어야 합니다."))
                .andExpect(jsonPath("$.path").value("/members"));

        verify(memberService, never()).createMember(any());
    }

    @Test
    @DisplayName("회원 조회 성공: 존재하는 회원이면 200과 회원 정보를 반환한다")
    void getMember_success() throws Exception {
        MemberResponseDto response = memberResponse("이펍");
        when(memberService.getMember(MEMBER_ID)).thenReturn(response);

        mockMvc.perform(get("/members/{memberId}", MEMBER_ID)
                        .with(authenticatedMember(MEMBER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(MEMBER_ID))
                .andExpect(jsonPath("$.nickname").value("이펍"))
                .andExpect(jsonPath("$.email").value("member@example.com"))
                .andExpect(jsonPath("$.profileImage").value("https://example.com/profile.png"));

        verify(memberService).getMember(MEMBER_ID);
    }

    @Test
    @DisplayName("회원 조회 실패: 존재하지 않는 회원이면 404를 반환한다")
    void getMember_fail_notFound() throws Exception {
        when(memberService.getMember(999L))
                .thenThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(get("/members/{memberId}", 999L)
                        .with(authenticatedMember(MEMBER_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/members/999"));

        verify(memberService).getMember(999L);
    }

    @Test
    @DisplayName("회원 닉네임 수정 성공: 본인이 요청하면 200과 수정된 정보를 반환한다")
    void updateMember_success() throws Exception {
        UpdateMemberNicknameRequestDto request = new UpdateMemberNicknameRequestDto("새닉네임");
        MemberResponseDto response = memberResponse("새닉네임");
        when(memberService.updateMember(MEMBER_ID, request)).thenReturn(response);

        mockMvc.perform(patch("/members/profile/{memberId}", MEMBER_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(MEMBER_ID))
                .andExpect(jsonPath("$.nickname").value("새닉네임"));

        verify(memberService).updateMember(MEMBER_ID, request);
    }

    @Test
    @DisplayName("회원 닉네임 수정 실패: 다른 회원의 정보는 수정할 수 없다")
    void updateMember_fail_accessDenied() throws Exception {
        UpdateMemberNicknameRequestDto request = new UpdateMemberNicknameRequestDto("새닉네임");

        mockMvc.perform(patch("/members/profile/{memberId}", MEMBER_ID)
                        .with(authenticatedMember(2L))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.path").value("/members/profile/1"));

        verify(memberService, never()).updateMember(any(), any());
    }

    @Test
    @DisplayName("회원 탈퇴 성공: 본인이 요청하면 200과 성공 메시지를 반환한다")
    void deleteMember_success() throws Exception {
        doNothing().when(memberService).deleteMember(MEMBER_ID);

        mockMvc.perform(patch("/members/{memberId}", MEMBER_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 탈퇴되었습니다."));

        verify(memberService).deleteMember(MEMBER_ID);
    }

    @Test
    @DisplayName("회원 탈퇴 실패: 다른 회원을 탈퇴시킬 수 없다")
    void deleteMember_fail_accessDenied() throws Exception {
        mockMvc.perform(patch("/members/{memberId}", MEMBER_ID)
                        .with(authenticatedMember(2L))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(memberService, never()).deleteMember(any());
    }

    @Test
    @DisplayName("회원 물리 삭제 성공: 본인이 요청하면 200과 성공 메시지를 반환한다")
    void physicalDeleteMember_success() throws Exception {
        doNothing().when(memberService).physicalDeleteMember(MEMBER_ID);

        mockMvc.perform(delete("/members/{memberId}", MEMBER_ID)
                        .with(authenticatedMember(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 삭제되었습니다."));

        verify(memberService).physicalDeleteMember(MEMBER_ID);
    }

    @Test
    @DisplayName("회원 물리 삭제 실패: 존재하지 않는 회원이면 404를 반환한다")
    void physicalDeleteMember_fail_notFound() throws Exception {
        doThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND))
                .when(memberService).physicalDeleteMember(999L);

        mockMvc.perform(delete("/members/{memberId}", 999L)
                        .with(authenticatedMember(999L))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/members/999"));

        verify(memberService).physicalDeleteMember(999L);
    }

    private CreateMemberRequestDto createMemberRequest() {
        return new CreateMemberRequestDto(
                "20240001",
                "이화여자대학교",
                "이펍",
                "member@example.com",
                "password123!"
        );
    }

    private MemberResponseDto memberResponse(String nickname) {
        return new MemberResponseDto(
                MEMBER_ID,
                nickname,
                "member@example.com",
                "https://example.com/profile.png"
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
