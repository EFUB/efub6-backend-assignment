package efub.assignment.community.global.security.jwt;

import java.time.Instant;

public record JwtTokenPair(
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
