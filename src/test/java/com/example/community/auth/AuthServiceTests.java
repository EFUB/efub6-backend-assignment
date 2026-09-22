package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.MemberStatus;
import com.example.community.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTests {
    private final MemberRepository members = mock(MemberRepository.class);
    private final JwtService jwt = mock(JwtService.class);
    private final RefreshTokenStore store = mock(RefreshTokenStore.class);
    private final AuthService auth = new AuthService(members, jwt, store);
    private final Member member = mock(Member.class);
    private final Map<String, Object> attributes = Map.of("id", 123L, "kakao_account", Map.of("email", "member@example.com"));

    @BeforeEach void setup() {
        when(member.getMemberId()).thenReturn(7L);
        when(member.getStatus()).thenReturn(MemberStatus.REGISTER);
    }

    @Test void existingMemberLoginStoresRefresh() {
        when(members.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(jwt.access(7L)).thenReturn("test-access");
        var refresh = new JwtService.IssuedRefresh("test-refresh", Instant.now().plusSeconds(100));
        when(jwt.refresh(7L)).thenReturn(refresh);
        var response = auth.login(attributes);
        assertEquals("test-access", response.accessToken());
        assertEquals("test-refresh", response.refreshToken());
        verify(store).save(7L, refresh);
        verify(members, never()).save(any());
    }

    @Test void unknownMemberIsNotCreated() {
        when(members.findByEmail(anyString())).thenReturn(Optional.empty());
        assertCode(ErrorCode.MEMBER_NOT_FOUND, () -> auth.login(attributes));
        verify(members, never()).save(any());
        verifyNoInteractions(jwt, store);
    }

    @Test void withdrawnMemberCannotLogin() {
        when(members.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(member.getStatus()).thenReturn(MemberStatus.UNREGISTER);
        assertCode(ErrorCode.MEMBER_UNREGISTERED, () -> auth.login(attributes));
        verifyNoInteractions(jwt, store);
    }

    @Test void missingKakaoIdOrEmailIsRejected() {
        assertCode(ErrorCode.INVALID_KAKAO_USER, () -> auth.login(Map.of("id", 123)));
        assertCode(ErrorCode.INVALID_KAKAO_USER, () -> auth.login(Map.of("kakao_account", Map.of("email", "a@b.com"))));
        verifyNoInteractions(members, jwt, store);
    }

    @Test void explicitlyUnverifiedEmailIsRejected() {
        assertCode(ErrorCode.INVALID_KAKAO_USER, () -> auth.login(Map.of("id", 123,
                "kakao_account", Map.of("email", "a@b.com", "is_email_verified", false))));
    }

    @Test void reissueReturnsOnlyAccessWithoutRotation() {
        when(jwt.validateRefresh("test-refresh")).thenReturn(7L);
        when(members.findById(7L)).thenReturn(Optional.of(member));
        when(store.matches(7L, "test-refresh")).thenReturn(true);
        when(jwt.access(7L)).thenReturn("new-access");
        assertEquals("new-access", auth.reissue("test-refresh").accessToken());
        verify(store, never()).save(any(), any());
        verify(jwt, never()).refresh(any());
    }

    @Test void withdrawnMemberCannotReissue() {
        when(jwt.validateRefresh("test-refresh")).thenReturn(7L);
        when(members.findById(7L)).thenReturn(Optional.of(member));
        when(member.getStatus()).thenReturn(MemberStatus.UNREGISTER);
        assertCode(ErrorCode.MEMBER_UNREGISTERED, () -> auth.reissue("test-refresh"));
        verifyNoInteractions(store);
    }

    @Test void redisMismatchOrMissingTokenIsRejected() {
        when(jwt.validateRefresh("test-refresh")).thenReturn(7L);
        when(members.findById(7L)).thenReturn(Optional.of(member));
        assertCode(ErrorCode.REFRESH_TOKEN_MISMATCH, () -> auth.reissue("test-refresh"));
        verify(jwt, never()).access(any());
    }

    private void assertCode(ErrorCode expected, org.junit.jupiter.api.function.Executable action) {
        assertEquals(expected, assertThrows(CustomException.class, action).getErrorCode());
    }
}
