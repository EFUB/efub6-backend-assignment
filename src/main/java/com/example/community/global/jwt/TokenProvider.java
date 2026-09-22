package com.example.community.global.jwt;

import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private SecretKey key; // 매 요청마다 새로 만들 필요 없이 1회 생성 후 재사용

    private static final Long ACCESS_TOKEN_EXPIRATION = 1000*60*60L; // 1시간
    private static final Long REFRESH_TOKEN_EXPIRATION = 1000*60*60*24*14L; // 2주

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Member member) {
        return buildToken(member.getEmail(), ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(Member member) {
        return buildToken(member.getEmail(), REFRESH_TOKEN_EXPIRATION);
    }

    public void saveRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(memberId.toString(), refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));
    }

    public String extractEmail(String accessToken) {
        try {
            return getClaims(accessToken).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid or expired token", e);
            return null;
        }
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            log.info("Validate token success");
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        Set<SimpleGrantedAuthority> authorities = Collections
                .singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails
                .User(claims.getSubject(), "", authorities), token, authorities);
    }

    private String buildToken(String subject, long expirationMillis) {
        Date now = new Date();
        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
