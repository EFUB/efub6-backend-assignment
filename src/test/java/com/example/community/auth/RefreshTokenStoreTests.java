package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.time.Duration;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenStoreTests {
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    private final ValueOperations<String, String> values = mock(ValueOperations.class);
    private final RefreshTokenStore store = new RefreshTokenStore(redis);

    @Test void storesPerMemberWithRemainingTtl() {
        when(redis.opsForValue()).thenReturn(values);
        store.save(7L, new JwtService.IssuedRefresh("test-refresh", Instant.now().plusSeconds(60)));
        var ttl = ArgumentCaptor.forClass(Duration.class);
        verify(values).set(eq("auth:refresh:7"), eq("test-refresh"), ttl.capture());
        assertTrue(ttl.getValue().toMillis() > 58000 && ttl.getValue().toMillis() <= 60000);
    }

    @Test void matchesAndDeletesOnlyMemberKey() {
        when(redis.opsForValue()).thenReturn(values);
        when(values.get("auth:refresh:7")).thenReturn("test-refresh");
        assertTrue(store.matches(7L, "test-refresh"));
        assertFalse(store.matches(7L, "other"));
        assertFalse(store.matches(8L, "test-refresh"));
        store.delete(7L);
        verify(redis).delete("auth:refresh:7");
    }

    @Test void redisFailureIsMappedWithoutLeakingDetails() {
        when(redis.opsForValue()).thenThrow(new RedisConnectionFailureException("unavailable"));
        assertThrows(CustomException.class, () -> store.matches(7L, "test-refresh"));
    }
}
