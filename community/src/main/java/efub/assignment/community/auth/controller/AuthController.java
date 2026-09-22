package efub.assignment.community.auth.controller;

import efub.assignment.community.auth.dto.request.RefreshTokenRequest;
import efub.assignment.community.auth.dto.response.AccessTokenResponse;
import efub.assignment.community.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
