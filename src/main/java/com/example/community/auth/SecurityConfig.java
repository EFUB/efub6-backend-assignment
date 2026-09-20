package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.global.exception.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.community.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthService auth;
    private final ObjectMapper mapper;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwt, MemberRepository members) throws Exception {
        // Existing APIs still identify members via Auth-Id; no session authentication is persisted.
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> requests.requestMatchers("/auth/me").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(
                        (request, response, exception) -> JwtAuthenticationFilter.unauthorized(request, response, mapper)))
                .addFilterBefore(new JwtAuthenticationFilter(jwt, members, mapper), UsernamePasswordAuthenticationFilter.class)
                .requestCache(cache -> cache.disable())
                .securityContext(context -> context.securityContextRepository(new NullSecurityContextRepository()))
                .oauth2Login(oauth -> oauth
                        .authorizedClientRepository(new OAuth2AuthorizedClientRepository() {
                            @Override
                            public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String id, Authentication principal, HttpServletRequest request) { return null; }
                            @Override
                            public void saveAuthorizedClient(OAuth2AuthorizedClient client, Authentication principal, HttpServletRequest request, HttpServletResponse response) { /* Do not store Kakao tokens. */ }
                            @Override
                            public void removeAuthorizedClient(String id, Authentication principal, HttpServletRequest request, HttpServletResponse response) {}
                        })
                        .successHandler((request, response, authentication) -> {
                            try {
                                OAuth2User user = (OAuth2User) authentication.getPrincipal();
                                write(response, 200, auth.login(user.getAttributes()));
                            } catch (CustomException e) {
                                error(request, response, e.getErrorCode());
                            } finally {
                                clearSession(request);
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            clearSession(request);
                            error(request, response, ErrorCode.OAUTH_LOGIN_FAILED);
                        }));
        return http.build();
    }

    private void clearSession(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) request.getSession(false).invalidate();
    }

    private void error(HttpServletRequest request, HttpServletResponse response, ErrorCode code) throws IOException {
        write(response, code.getStatus(), new ErrorDto(LocalDateTime.now().toString(), code.getStatus(),
                code.name(), code.getMessage(), request.getRequestURI()));
    }

    private void write(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        mapper.writeValue(response.getWriter(), body);
    }
}
