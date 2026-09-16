package efub.assignment.community.auth.controller;

import efub.assignment.community.auth.dto.request.ReissueTokenRequestDto;
import efub.assignment.community.auth.dto.response.ReissueTokenResponseDto;
import efub.assignment.community.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/token")
    public ResponseEntity<ReissueTokenResponseDto> reissueAccessToken(@RequestBody ReissueTokenRequestDto request) {

        ReissueTokenResponseDto response = authService.reissueAccessToken(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
