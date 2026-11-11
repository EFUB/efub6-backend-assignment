package efub.assignment.community.member.controller;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private ObjectMapper objectMapper;

    CreateMemberRequestDto request;

    @BeforeEach
    void setUp() {
        request = new CreateMemberRequestDto("비밀번호1", "efub123@example.com", "닉네임12", "이화여자대학교", "1234567~");
    }

    @Test
    @DisplayName("POST /members -> 201, Location 헤더 & H2에 실제 저장")
    void createPost_and_persist() throws Exception {
        //when
        MvcResult result = mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("^/members/\\d+$")))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        long id = Long.parseLong(URI.create(location).getPath().replace("/members/", ""));

        assertTrue(memberRepository.findByMemberId(id).isPresent());
    }

    @Test
    @DisplayName("GET /member/{id} -> 200 응답 & 응답 필드 검증")
    void getPost_200() throws Exception {
        //given
        Member member = Member.builder()
                .nickname("닉네임")
                .email("efub@example.com")
                .studentId("1234567")
                .password("비밀번호")
                .university("이화여자대학교")
                .build();
        memberRepository.save(member);

        //when & then
        mockMvc.perform(get("/members/{id}", member.getMemberId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("닉네임"))
                .andExpect((jsonPath("$.email").value("efub@example.com")))
                .andExpect(jsonPath("$.studentId").value("1234567"))
                .andExpect(jsonPath("$.university").value("이화여자대학교"));
    }

}
