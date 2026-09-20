package efub.assignment.community.global.security.jwt;

import java.time.Instant;

public record JwtTokenClaims(
        Long memberId,
        JwtTokenType tokenType,
        String role,
        String tokenId,
        Instant expiresAt
) {
}
