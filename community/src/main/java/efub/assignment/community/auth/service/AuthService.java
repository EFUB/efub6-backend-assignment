package efub.assignment.community.auth.service;

import efub.assignment.community.auth.dto.response.AuthTokenResponse;
import efub.assignment.community.auth.dto.response.AccessTokenResponse;
import efub.assignment.community.auth.repository.RefreshTokenRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.security.jwt.InvalidJwtTokenException;
import efub.assignment.community.global.security.jwt.JwtTokenClaims;
import efub.assignment.community.global.security.jwt.JwtTokenPair;
import efub.assignment.community.global.security.jwt.JwtTokenProvider;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String USER_ROLE = "ROLE_USER";

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public AuthTokenResponse issueTokens(Long memberId) {
        JwtTokenPair tokenPair = jwtTokenProvider.createTokenPair(memberId, USER_ROLE);
        refreshTokenRepository.save(
                memberId,
                tokenPair.refreshToken(),
                tokenPair.refreshTokenExpiresAt()
        );
        return AuthTokenResponse.from(memberId, tokenPair);
    }

    public AccessTokenResponse refresh(String refreshToken) {
        JwtTokenClaims claims = parseRefreshToken(refreshToken);
        Long memberId = claims.memberId();

        if (!refreshTokenRepository.matches(memberId, refreshToken)) {
            throw invalidRefreshToken();
        }

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null || member.getStatus() != MemberStatus.REGISTER) {
            refreshTokenRepository.delete(memberId);
            throw invalidRefreshToken();
        }

        return new AccessTokenResponse(
                jwtTokenProvider.createAccessToken(memberId, USER_ROLE)
        );
    }

    public void logout(String refreshToken) {
        JwtTokenClaims claims = parseRefreshToken(refreshToken);
        Long memberId = claims.memberId();

        if (!refreshTokenRepository.matches(memberId, refreshToken)) {
            throw invalidRefreshToken();
        }

        refreshTokenRepository.delete(memberId);
    }

    private JwtTokenClaims parseRefreshToken(String refreshToken) {
        try {
            return jwtTokenProvider.parseRefreshToken(refreshToken);
        } catch (InvalidJwtTokenException exception) {
            throw invalidRefreshToken();
        }
    }

    private CustomException invalidRefreshToken() {
        return new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
