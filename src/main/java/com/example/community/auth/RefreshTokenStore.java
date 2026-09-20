package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {
    private final StringRedisTemplate redis;
    private String key(Long memberId) { return "auth:refresh:" + memberId; }

    public void save(Long memberId, JwtService.IssuedRefresh refresh) {
        try {
            redis.opsForValue().set(key(memberId), refresh.token(), Duration.between(Instant.now(), refresh.expiresAt()));
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }

    public boolean matches(Long memberId, String token) {
        try {
            return token.equals(redis.opsForValue().get(key(memberId)));
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }

    public void delete(Long memberId) {
        try {
            redis.delete(key(memberId));
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }
}
