package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.MemberStatus;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository members;
    private final JwtService jwt;
    private final RefreshTokenStore tokens;

    public LoginResponse login(Map<String, Object> attributes) {
        Object id = attributes.get("id");
        Object account = attributes.get("kakao_account");
        if (id == null || id.toString().isBlank() || !(account instanceof Map<?, ?> info)
                || !(info.get("email") instanceof String email) || email.isBlank()
                || Boolean.FALSE.equals(info.get("is_email_valid"))
                || Boolean.FALSE.equals(info.get("is_email_verified"))) {
            throw new CustomException(ErrorCode.INVALID_KAKAO_USER);
        }
        Member member = members.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        checkStatus(member);
        String access = jwt.access(member.getMemberId());
        JwtService.IssuedRefresh refresh = jwt.refresh(member.getMemberId());
        tokens.save(member.getMemberId(), refresh);
        return new LoginResponse(access, refresh.token());
    }

    public AccessResponse reissue(String refreshToken) {
        Long memberId = jwt.validateRefresh(refreshToken);
        Member member = members.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        checkStatus(member);
        if (!tokens.matches(memberId, refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }
        return new AccessResponse(jwt.access(memberId));
    }

    private void checkStatus(Member member) {
        if (member.getStatus() != MemberStatus.REGISTER) {
            throw new CustomException(ErrorCode.MEMBER_UNREGISTERED);
        }
    }

    public record LoginResponse(String accessToken, String refreshToken) {}
    public record AccessResponse(String accessToken) {}
}
