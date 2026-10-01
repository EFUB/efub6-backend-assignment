package efub.assignment.community.global.handler;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.jwt.TokenProvider;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // OAuth2 인증된 사용자 정보 가져오기
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        // 사용자 속성에서 이메일 추출
        //String email = (String) oAuth2User.getAttributes().get("email");

        // 사용자 속성에서 닉네임 추출
        String nickname = (String) oAuth2User.getAttributes().get("nickname");

        // email을 통해 데이터베이스에서 User 엔티티 조회
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new EntityNotFoundException("해당 email을 가진 Member를 찾을 수 없습니다. email=" + email));

        // nickname을 통해 데이터베이스에서 User 엔티티 조회
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_WITH_NICKNAME));

        // AccessToken, RefreshToken 발급
        String accessToken = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member);

        tokenProvider.saveRefreshToken(member.getMemberId(), refreshToken);

        // JSON 형식 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format("{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}", accessToken, refreshToken);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
