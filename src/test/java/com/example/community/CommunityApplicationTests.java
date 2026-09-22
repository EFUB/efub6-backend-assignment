package com.example.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.community.auth.JwtService;
import com.example.community.auth.RefreshTokenStore;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.MemberStatus;
import com.example.community.member.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.security.oauth2.client.registration.kakao.client-id=test-client",
        "spring.security.oauth2.client.registration.kakao.client-secret=test-secret",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
@AutoConfigureMockMvc
class CommunityApplicationTests {
    private static final SecretKey TEST_KEY = Jwts.SIG.HS256.key().build();

    @DynamicPropertySource
    static void jwtSecret(DynamicPropertyRegistry registry) {
        String secret = Encoders.BASE64.encode(TEST_KEY.getEncoded());
        registry.add("jwt.secret", () -> secret);
    }

    @Autowired MockMvc mvc;
    @Autowired MemberRepository members;
    @Autowired JwtService jwt;

    @MockitoBean RefreshTokenStore store;

    @Test
    void meUsesAccessTokenMemberAndReturnsOnlyRequestedFields() throws Exception {
        Member member = createMember();
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + jwt.access(member.getMemberId()))
                        .header("Auth-Id", Long.MAX_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getMemberId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.status").value("REGISTER"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.aMapWithSize(4)))
                .andExpect(header().string("Cache-Control", "no-store"));
        // Authentication must not leak to the next request or be inferred from Auth-Id.
        mvc.perform(get("/auth/me").header("Auth-Id", member.getMemberId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meRejectsRefreshToken() throws Exception {
        assertMeUnauthorized(jwt.refresh(createMember().getMemberId()).token());
    }

    @Test
    void meRejectsExpiredAccessToken() throws Exception {
        String token = Jwts.builder().subject(createMember().getMemberId().toString())
                .claim("tokenType", "ACCESS").expiration(Date.from(Instant.now().minusSeconds(60)))
                .signWith(TEST_KEY).compact();
        assertMeUnauthorized(token);
    }

    @Test
    void meRejectsMalformedAndWrongSignatureTokens() throws Exception {
        assertMeUnauthorized("invalid");
        String token = Jwts.builder().subject(createMember().getMemberId().toString())
                .claim("tokenType", "ACCESS").expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(Jwts.SIG.HS256.key().build()).compact();
        assertMeUnauthorized(token);
    }

    @Test
    void meRejectsWithdrawnMemberEvenWithPreviouslyIssuedAccess() throws Exception {
        Member member = createMember();
        String token = jwt.access(member.getMemberId());
        member.changeStatus(MemberStatus.UNREGISTER);
        members.saveAndFlush(member);
        assertMeUnauthorized(token);
    }

    @Test
    void meRejectsMissingMemberAndMalformedAuthorization() throws Exception {
        assertMeUnauthorized(jwt.access(Long.MAX_VALUE));
        for (String header : new String[]{"Bearer ", "Basic invalid"}) {
            mvc.perform(get("/auth/me").header("Authorization", header))
                    .andExpect(status().isUnauthorized());
        }
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_TOKEN"));
    }

    @Test
    void meRejectsMissingTypeOrExpiry() throws Exception {
        String subject = createMember().getMemberId().toString();
        assertMeUnauthorized(Jwts.builder().subject(subject).claim("tokenType", "ACCESS")
                .signWith(TEST_KEY).compact());
        assertMeUnauthorized(Jwts.builder().subject(subject).expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(TEST_KEY).compact());
    }

    private Member createMember() {
        return members.saveAndFlush(Member.builder().email(UUID.randomUUID() + "@example.com")
                .password("test-only").nickname("test").university("test").studentId("123").build());
    }

    private void assertMeUnauthorized(String token) throws Exception {
        mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_TOKEN"));
    }

    @Test
    void kakaoLoginStartsWithRedirect() throws Exception {
        mvc.perform(get("/oauth2/authorization/kakao"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("https://kauth.kakao.com/oauth/authorize")));
    }

    @Test
    void invalidRefreshReturnsJsonError() throws Exception {
        mvc.perform(post("/auth/token")
                .contentType("application/json").content("{\"refreshToken\":\"invalid\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_TOKEN"));
    }

    @Test
    void blankRefreshReturnsBadRequest() throws Exception {
        mvc.perform(post("/auth/token")
                .contentType("application/json").content("{\"refreshToken\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reissueThenWithdrawalRevokesRefresh() throws Exception {
        Member member = members.save(Member.builder().email("integration@example.com").password("test-only")
                .nickname("test").university("test").studentId("123").build());
        Long id = member.getMemberId();
        String refresh = jwt.refresh(id).token();
        when(store.matches(id, refresh)).thenReturn(true);
        String body = "{\"refreshToken\":\"" + refresh + "\"}";
        mvc.perform(post("/auth/token").contentType("application/json").content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(header().string("Cache-Control", "no-store"));
        verify(store, never()).save(any(), any());
        mvc.perform(patch("/members/{memberId}", id)).andExpect(status().isNoContent());
        verify(store).delete(id);
        assertEquals(MemberStatus.UNREGISTER, members.findById(id).orElseThrow().getStatus());
        mvc.perform(post("/auth/token").contentType("application/json").content(body))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.errorCode").value("MEMBER_UNREGISTERED"));
    }

	@Test
	void contextLoads() {
	}

}
