package efub.assignment.community.auth.controller;

import efub.assignment.community.auth.dto.AuthMemberResponseDto;
import efub.assignment.community.auth.dto.TokenRequestDto;
import efub.assignment.community.auth.dto.TokenResponseDto;
import efub.assignment.community.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 현재 인증된 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<AuthMemberResponseDto> getCurrentMember() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.getCurrentMember());
    }

    // AccessToken 재발급
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> reissueAccessToken(
            @RequestBody TokenRequestDto requestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.reissueAccessToken(
                        requestDto.getRefreshToken()
                ));
    }
}