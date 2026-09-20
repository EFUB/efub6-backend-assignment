package efub.assignment.community.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "auth:refresh:";

    private final StringRedisTemplate redisTemplate;
    private final Clock clock;

    public void save(Long memberId, String refreshToken, Instant expiresAt) {
        validateToken(refreshToken);
        Objects.requireNonNull(expiresAt, "Refresh token 만료 시각은 필수입니다.");

        Duration ttl = Duration.between(clock.instant(), expiresAt);
        if (ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("만료된 Refresh token은 저장할 수 없습니다.");
        }

        redisTemplate.opsForValue().set(
                key(memberId),
                encodeHash(refreshToken),
                ttl
        );
    }

    public boolean matches(Long memberId, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return false;
        }

        Optional<String> storedHash = Optional.ofNullable(
                redisTemplate.opsForValue().get(key(memberId))
        );
        if (storedHash.isEmpty()) {
            return false;
        }

        byte[] storedHashBytes;
        try {
            storedHashBytes = Base64.getUrlDecoder().decode(storedHash.get());
        } catch (IllegalArgumentException exception) {
            return false;
        }

        return MessageDigest.isEqual(storedHashBytes, hash(refreshToken));
    }

    public void delete(Long memberId) {
        redisTemplate.delete(key(memberId));
    }

    private String key(Long memberId) {
        return KEY_PREFIX + Objects.requireNonNull(memberId, "멤버 ID는 필수입니다.");
    }

    private void validateToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token은 필수입니다.");
        }
    }

    private String encodeHash(String refreshToken) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash(refreshToken));
    }

    private byte[] hash(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", exception);
        }
    }
}
