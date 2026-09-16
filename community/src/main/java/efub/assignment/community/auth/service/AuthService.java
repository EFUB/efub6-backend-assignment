package efub.assignment.community.auth.service;

import efub.assignment.community.auth.dto.request.ReissueTokenRequestDto;
import efub.assignment.community.auth.dto.response.ReissueTokenResponseDto;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.jwt.JwtUtil;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public ReissueTokenResponseDto reissueAccessToken(ReissueTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtUtil.extractEmail(refreshToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String storedRefreshToken = redisTemplate.opsForValue().get(member.getMemberId().toString());

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException((ErrorCode.INVALID_REFRESH_TOKEN));
        }

        String accessToken = jwtUtil.createAccessToken(member);

        return ReissueTokenResponseDto.builder()
                .accessToken(accessToken)
                .build();

    }
}
