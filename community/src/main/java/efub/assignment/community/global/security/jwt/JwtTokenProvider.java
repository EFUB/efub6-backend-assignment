package efub.assignment.community.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final String ISSUER = "efub-community";
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ROLE_CLAIM = "role";

    private final JwtProperties properties;
    private final Clock clock;
    private final SecretKey signingKey;
    private final JwtParser jwtParser;

    public JwtTokenProvider(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        this.signingKey = createSigningKey(properties.secret());
        this.jwtParser = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(ISSUER)
                .clock(() -> Date.from(clock.instant()))
                .build();
    }

    public JwtTokenPair createTokenPair(Long memberId, String role) {
        validateIssueRequest(memberId, role);

        Instant now = clock.instant();
        Instant issuedAt = now.truncatedTo(ChronoUnit.SECONDS);
        Instant accessTokenExpiresAt = now
                .plus(properties.accessTokenValidity())
                .truncatedTo(ChronoUnit.SECONDS);
        Instant refreshTokenExpiresAt = now
                .plus(properties.refreshTokenValidity())
                .truncatedTo(ChronoUnit.SECONDS);

        String accessToken = createToken(
                memberId,
                JwtTokenType.ACCESS,
                role,
                issuedAt,
                accessTokenExpiresAt
        );
        String refreshToken = createToken(
                memberId,
                JwtTokenType.REFRESH,
                null,
                issuedAt,
                refreshTokenExpiresAt
        );

        return new JwtTokenPair(
                accessToken,
                refreshToken,
                accessTokenExpiresAt,
                refreshTokenExpiresAt
        );
    }

    public String createAccessToken(Long memberId, String role) {
        validateIssueRequest(memberId, role);

        Instant now = clock.instant();
        return createToken(
                memberId,
                JwtTokenType.ACCESS,
                role,
                now.truncatedTo(ChronoUnit.SECONDS),
                now.plus(properties.accessTokenValidity())
                        .truncatedTo(ChronoUnit.SECONDS)
        );
    }

    private void validateIssueRequest(Long memberId, String role) {
        if (memberId == null) {
            throw new IllegalArgumentException("멤버 ID는 필수입니다.");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("회원 권한은 필수입니다.");
        }
    }

    public JwtTokenClaims parseAccessToken(String token) {
        return parseToken(token, JwtTokenType.ACCESS);
    }

    public JwtTokenClaims parseRefreshToken(String token) {
        return parseToken(token, JwtTokenType.REFRESH);
    }

    public boolean isValid(String token, JwtTokenType expectedType) {
        try {
            parseToken(token, expectedType);
            return true;
        } catch (InvalidJwtTokenException exception) {
            return false;
        }
    }

    private String createToken(
            Long memberId,
            JwtTokenType tokenType,
            String role,
            Instant issuedAt,
            Instant expiresAt
    ) {
        JwtBuilder builder = Jwts.builder()
                .issuer(ISSUER)
                .subject(memberId.toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .claim(TOKEN_TYPE_CLAIM, tokenType.claimValue());

        if (role != null) {
            builder.claim(ROLE_CLAIM, role);
        }

        return builder
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    private JwtTokenClaims parseToken(String token, JwtTokenType expectedType) {
        if (token == null || token.isBlank()) {
            throw new InvalidJwtTokenException("JWT가 비어 있습니다.");
        }

        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();

            JwtTokenType actualType = tokenTypeFrom(claims.get(TOKEN_TYPE_CLAIM, String.class));
            if (actualType != expectedType) {
                throw new InvalidJwtTokenException("JWT 종류가 올바르지 않습니다.");
            }

            Long memberId;
            try {
                memberId = Long.valueOf(claims.getSubject());
            } catch (NumberFormatException | NullPointerException exception) {
                throw new InvalidJwtTokenException("JWT의 멤버 ID가 올바르지 않습니다.", exception);
            }

            String role = claims.get(ROLE_CLAIM, String.class);
            if (actualType == JwtTokenType.ACCESS && (role == null || role.isBlank())) {
                throw new InvalidJwtTokenException("Access token에 회원 권한이 없습니다.");
            }

            Date expiration = claims.getExpiration();
            String tokenId = claims.getId();
            if (expiration == null || tokenId == null || tokenId.isBlank()) {
                throw new InvalidJwtTokenException("JWT 필수 정보가 없습니다.");
            }

            return new JwtTokenClaims(
                    memberId,
                    actualType,
                    role,
                    tokenId,
                    expiration.toInstant()
            );
        } catch (InvalidJwtTokenException exception) {
            throw exception;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidJwtTokenException("유효하지 않은 JWT입니다.", exception);
        }
    }

    private JwtTokenType tokenTypeFrom(String claimValue) {
        for (JwtTokenType tokenType : JwtTokenType.values()) {
            if (tokenType.claimValue().equals(claimValue)) {
                return tokenType;
            }
        }
        throw new InvalidJwtTokenException("JWT 종류 정보가 올바르지 않습니다.");
    }

    private SecretKey createSigningKey(String secret) {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(
                    "JWT secret은 Base64로 인코딩된 256비트 이상의 값이어야 합니다.",
                    exception
            );
        }
    }
}
