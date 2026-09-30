package com.example.community.member.controller;

import com.example.community.member.dto.request.CreateMemberRequestDto;
import com.example.community.member.dto.request.UpdateMemberRequestDto;
import com.example.community.member.dto.response.CreateMemberResponseDto;
import com.example.community.member.dto.response.MemberResponseDto;
import com.example.community.member.service.MemberService;
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

import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(profiles = "test")
@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MemberService memberService;

    private Long memberId = 1L;


    @Nested
    @DisplayName("GET /members/{memberId} - 회원 조회")
    class GetMember {
        @Test
        @DisplayName("존재하는 회원 ID로 요청하면 회원 정보와 200 OK를 반환한다")
        void getMember_whenMemberExists_returnsMember() throws Exception {
            // given
            MemberResponseDto response = new MemberResponseDto(
                    memberId, "e@email.com", "닉네임", "대학교", "111"
            );
            given(memberService.getMember(memberId)).willReturn(response);

            // when & then
            mockMvc.perform(get("/members/{memberId}", memberId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.memberId").value(memberId))
                    .andExpect(jsonPath("$.nickname").value(response.nickname()));
            verify(memberService).getMember(memberId);
        }

        @Test
        @DisplayName("회원 ID가 숫자가 아니면 400 Bad Request를 반환한다")
        void getMember_whenMemberIdIsInvalid_returnsBadRequest() throws Exception {
            // when & then
            mockMvc.perform(get("/members/{memberId}", "invalid"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(memberService);
        }
    }

    @Nested
    @DisplayName("POST /members - 회원 생성")
    class CreateMember {
        @Test
        @DisplayName("유효한 회원 정보로 요청하면 회원을 생성하고 201 Created를 반환한다")
        void createMember_whenRequestIsValid_returnsCreated() throws Exception {
            // given
            CreateMemberRequestDto request = new CreateMemberRequestDto(
                    "e@email.com", "password", "닉네임",
                    "대학교", "111"
            );
            CreateMemberResponseDto response = new CreateMemberResponseDto(
                    memberId, "e@email.com", "닉네임", "대학교", "111"
            );
            given(memberService.createMember(request)).willReturn(response);

            // when & then
            mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.memberId").value(memberId))
                    .andExpect(jsonPath("$.university").value(response.university()));
            verify(memberService).createMember(request);
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidCreateMemberRequests")
        @DisplayName("유효하지 않은 회원 정보로 요청하면 400 Bad Request를 반환한다")
        void createMember_whenRequestIsInvalid_returnsBadRequest(
                String description,
                CreateMemberRequestDto request
        ) throws Exception {
            // when & then
            mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(memberService);
        }

        static Stream<Arguments> invalidCreateMemberRequests() {
            return Stream.of(
                    Arguments.of(
                            "이메일 형식이 올바르지 않은 경우",
                            new CreateMemberRequestDto(
                                    "eee", "password", "닉네임",
                                    "대학교", "111"
                            )
                    ),
                    Arguments.of(
                            "비밀번호가 null인 경우",
                            new CreateMemberRequestDto(
                                    "e@email.com", null, "닉네임",
                                    "대학교", "111"
                            )
                    )
            );
        }
    }



    @Nested
    @DisplayName("PATCH /members/profile/{memberId} - 회원 수정")
    class UpdateMember {
        @Test
        @DisplayName("유효한 수정 정보가 전달되면 회원 정보를 수정하고 200 OK를 반환한다")
        void updateMember_whenRequestIsValid_returnsCreated() throws Exception{
            // given
            UpdateMemberRequestDto request = new UpdateMemberRequestDto(
                    "e@email.com", "닉네임"
            );
            MemberResponseDto response = new MemberResponseDto(
                    memberId, "e@email.com", "닉네임", "대학교", "111"
            );
            given(memberService.updateMember(memberId, request)).willReturn(response);

            // when & then
            mockMvc.perform(patch("/members/profile/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.memberId").value(memberId))
                    .andExpect(jsonPath("$.email").value(response.email()));
            verify(memberService).updateMember(memberId, request);
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidUpdateMemberRequests")
        @DisplayName("유효하지 않은 수정 정보가 전달되면 400 Bad Request를 반환한다")
        void updateMember_whenRequestIsInvalid_returnsBadRequest(
                String description,
                UpdateMemberRequestDto request
        ) throws Exception {
            // when & then
            mockMvc.perform(patch("/members/profile/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(memberService);
        }

        static Stream<Arguments> invalidUpdateMemberRequests() {
            return Stream.of(
                    Arguments.of(
                            "이메일 형식이 올바르지 않은 경우",
                            new UpdateMemberRequestDto(
                                    "invalidEmail", "닉네임"
                            )
                    ),
                    Arguments.of(
                            "닉네임이 50자를 초과하는 경우",
                            new UpdateMemberRequestDto(
                                    "e@email.com",
                                    "닉".repeat(51)
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("PATCH /members/{memberId} - 회원 논리 삭제")
    class DeleteMember {
        @Test
        @DisplayName("회원 삭제에 성공하면 204를 반환한다")
        void deleteMember_whenMemberExists_returnsNoContent() throws Exception {
            // when & then
            mockMvc.perform(patch("/members/{memberId}", memberId))
                    .andExpect(status().isNoContent());
            verify(memberService).deleteMember(memberId);
        }

        @Test
        @DisplayName("회원 ID가 숫자가 아니면 400 Bad Request를 반환한다")
        void deleteMember_whenMemberIdIsInvalid_returnsBadRequest() throws Exception {
            // when & then
            mockMvc.perform(patch("/members/{memberId}", "invalid"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(memberService);
        }
    }

}
