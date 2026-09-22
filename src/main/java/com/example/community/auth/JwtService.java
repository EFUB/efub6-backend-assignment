package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessSeconds;
    private final long refreshSeconds;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-seconds:1800}") long accessSeconds,
                      @Value("${jwt.refresh-seconds:1209600}") long refreshSeconds) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        if (accessSeconds <= 0 || refreshSeconds <= 0) {
            throw new IllegalArgumentException("JWT validity must be positive");
        }
        this.accessSeconds = accessSeconds;
        this.refreshSeconds = refreshSeconds;
    }

    public String access(Long memberId) {
        return create(memberId, "ACCESS", Instant.now().plusSeconds(accessSeconds));
    }

    public IssuedRefresh refresh(Long memberId) {
        Instant expiresAt = Instant.now().plusSeconds(refreshSeconds).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        return new IssuedRefresh(create(memberId, "REFRESH", expiresAt), expiresAt);
    }

    private String create(Long memberId, String type, Instant expiresAt) {
        return Jwts.builder().subject(memberId.toString()).id(UUID.randomUUID().toString())
                .claim("tokenType", type).issuedAt(new Date()).expiration(Date.from(expiresAt))
                .signWith(key, Jwts.SIG.HS256).compact();
    }

    public Long validateRefresh(String token) {
        return validate(token, "REFRESH");
    }

    public Long validateAccess(String token) {
        return validate(token, "ACCESS");
    }

    private Long validate(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            if (!expectedType.equals(claims.get("tokenType", String.class)) || claims.getExpiration() == null) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
            long memberId = Long.parseLong(claims.getSubject());
            if (memberId <= 0) throw new CustomException(ErrorCode.INVALID_TOKEN);
            return memberId;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public record IssuedRefresh(String token, Instant expiresAt) {}
}
