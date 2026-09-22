package efub.assignment.community.auth.service;

import efub.assignment.community.auth.dto.AuthMemberResponseDto;
import efub.assignment.community.auth.dto.TokenResponseDto;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.jwt.TokenProvider;
import efub.assignment.community.global.utils.SecurityUtils;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    //현재 인증된 사용자 조회
    @Transactional(readOnly = true)
    public AuthMemberResponseDto getCurrentMember() {

        Long memberId = SecurityUtils.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return AuthMemberResponseDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }

    // 액세스 토큰 재발급
    public TokenResponseDto reissueAccessToken(String refreshToken) {

        // RefreshToken에서 memberId 추출
        Long memberId = tokenProvider.extractMemberId(refreshToken);

        // memberId로 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // Redis에서 해당 memberId를 key로 하는 RefreshToken 조회
        String storedRefreshToken =
                redisTemplate.opsForValue()
                        .get(member.getMemberId().toString());

        // 전달받은 RefreshToken과 Redis의 RefreshToken 비교
        if (storedRefreshToken == null
                || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 새로운 AccessToken 생성
        String accessToken =
                tokenProvider.createAccessToken(member);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}