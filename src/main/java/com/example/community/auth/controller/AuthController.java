package com.example.community.auth.controller;

import com.example.community.auth.dto.request.TokenRequestDto;
import com.example.community.auth.dto.response.TokenResponseDto;
import com.example.community.auth.service.AuthService;
import com.example.community.auth.service.CustomOAuth2UserService;
import com.example.community.global.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<String> getEmail() {
        return ResponseEntity.status(HttpStatus.OK).body(SecurityUtils.getCurrentUserEmail());
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> reissuedAccessToken(@RequestBody TokenRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.reissueAccessToken(requestDto.getRefreshToken()));
    }
}
