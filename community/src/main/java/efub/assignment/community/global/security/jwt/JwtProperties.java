package efub.assignment.community.global.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessExpirationMs,
        long refreshExpirationMs
) {
    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret은 필수입니다.");
        }
        if (accessExpirationMs <= 0) {
            throw new IllegalArgumentException("Access token 만료 시간은 0보다 커야 합니다.");
        }
        if (refreshExpirationMs <= 0) {
            throw new IllegalArgumentException("Refresh token 만료 시간은 0보다 커야 합니다.");
        }
    }

    public Duration accessTokenValidity() {
        return Duration.ofMillis(accessExpirationMs);
    }

    public Duration refreshTokenValidity() {
        return Duration.ofMillis(refreshExpirationMs);
    }
}
