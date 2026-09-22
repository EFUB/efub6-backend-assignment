package efub.assignment.community.global.handler;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.global.jwt.JwtUtil;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(member);
        String refreshToken = jwtUtil.createRefreshToken(member);

        jwtUtil.saveRefreshToken(member.getMemberId(), refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format("{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}", accessToken, refreshToken);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();


    }
}
