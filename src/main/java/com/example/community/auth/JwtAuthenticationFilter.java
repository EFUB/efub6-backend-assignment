package com.example.community.auth;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.global.exception.dto.ErrorDto;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.MemberStatus;
import com.example.community.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final MemberRepository members;
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            try {
                if (!authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
                    throw new CustomException(ErrorCode.INVALID_TOKEN);
                }
                Long memberId = jwt.validateAccess(authorization.substring(7));
                Member member = members.findById(memberId)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
                if (member.getStatus() != MemberStatus.REGISTER) {
                    throw new CustomException(ErrorCode.INVALID_TOKEN);
                }
                var principal = new AuthenticatedMember(member.getMemberId(), member.getEmail(),
                        member.getNickname(), member.getStatus());
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(principal, null, List.of()));
                SecurityContextHolder.setContext(context);
            } catch (CustomException e) {
                SecurityContextHolder.clearContext();
                unauthorized(request, response, mapper);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    static void unauthorized(HttpServletRequest request, HttpServletResponse response, ObjectMapper mapper) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("WWW-Authenticate", "Bearer");
        response.setHeader("Cache-Control", "no-store");
        mapper.writeValue(response.getWriter(), new ErrorDto(LocalDateTime.now().toString(), 401,
                ErrorCode.INVALID_TOKEN.name(), ErrorCode.INVALID_TOKEN.getMessage(), request.getRequestURI()));
    }
}
