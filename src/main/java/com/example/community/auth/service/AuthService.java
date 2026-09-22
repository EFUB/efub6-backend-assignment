package com.example.community.auth.service;

import com.example.community.auth.dto.response.TokenResponseDto;
import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.global.jwt.TokenProvider;
import com.example.community.member.domain.Member;
import com.example.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

   public TokenResponseDto reissueAccessToken(String refreshToken) {
       String email = tokenProvider.extractEmail(refreshToken);
       Member member = memberService.findByEmail(email);

       String storedRefreshToken = redisTemplate.opsForValue().get(member.getMemberId().toString());

       if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
           throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
       }

       String accessToken = tokenProvider.createAccessToken(member);

       return TokenResponseDto.builder()
               .accessToken(accessToken)
               .build();
   }
}
