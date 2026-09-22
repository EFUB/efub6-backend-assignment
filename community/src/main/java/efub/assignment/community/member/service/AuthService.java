package efub.assignment.community.member.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.jwt.TokenProvider;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.dto.response.TokenResponseDto;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    public TokenResponseDto reissueAccessToken(String refreshToken) {
        String nickname = tokenProvider.extractNickname(refreshToken);
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

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
