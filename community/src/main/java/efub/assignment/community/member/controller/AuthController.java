package efub.assignment.community.member.controller;

import efub.assignment.community.global.util.SecurityUtils;
import efub.assignment.community.member.dto.request.TokenRequestDto;
import efub.assignment.community.member.dto.response.TokenResponseDto;
import efub.assignment.community.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<String> getNickname() {
        return ResponseEntity.status(HttpStatus.OK).body(SecurityUtils.getCurrentUserNickname());
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> reissuedAccessToken(@RequestBody TokenRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.reissueAccessToken(requestDto.getRefreshToken()));
    }
}
