package efub.assignment.community.auth.dto.response;

import efub.assignment.community.global.security.jwt.JwtTokenPair;

import java.time.Instant;

public record AuthTokenResponse(
        Long memberId,
        String tokenType,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    public static AuthTokenResponse from(Long memberId, JwtTokenPair tokenPair) {
        return new AuthTokenResponse(
                memberId,
                BEARER_TOKEN_TYPE,
                tokenPair.accessToken(),
                tokenPair.accessTokenExpiresAt(),
                tokenPair.refreshToken(),
                tokenPair.refreshTokenExpiresAt()
        );
    }
}
