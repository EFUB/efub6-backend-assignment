package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTests {
    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final JwtService jwt = new JwtService(Encoders.BASE64.encode(key.getEncoded()), 1800, 1209600);

    @Test void refreshContainsTypeSubjectAndMatchingExpiry() {
        var refresh = jwt.refresh(7L);
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(refresh.token()).getPayload();
        assertEquals(7L, jwt.validateRefresh(refresh.token()));
        assertEquals("REFRESH", claims.get("tokenType"));
        assertEquals(refresh.expiresAt(), claims.getExpiration().toInstant());
    }

    @Test void accessCannotBeUsedToRefresh() {
        assertThrows(CustomException.class, () -> jwt.validateRefresh(jwt.access(7L)));
    }

    @Test void expiredRefreshIsRejected() {
        String expired = Jwts.builder().subject("7").claim("tokenType", "REFRESH")
                .expiration(Date.from(Instant.now().minusSeconds(5))).signWith(key).compact();
        assertThrows(CustomException.class, () -> jwt.validateRefresh(expired));
    }

    @Test void wrongSignatureIsRejected() {
        var other = new JwtService(Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded()), 1800, 3600);
        assertThrows(CustomException.class, () -> jwt.validateRefresh(other.refresh(7L).token()));
    }

    @Test void malformedOrMissingExpiryIsRejected() {
        assertThrows(CustomException.class, () -> jwt.validateRefresh("invalid"));
        String noExpiry = Jwts.builder().subject("7").claim("tokenType", "REFRESH").signWith(key).compact();
        assertThrows(CustomException.class, () -> jwt.validateRefresh(noExpiry));
    }

    @Test void repeatedLoginsProduceDistinctRefreshTokens() {
        assertNotEquals(jwt.refresh(7L).token(), jwt.refresh(7L).token());
    }
}
