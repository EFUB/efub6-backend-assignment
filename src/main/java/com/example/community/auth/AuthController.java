package com.example.community.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedMember> me(@AuthenticationPrincipal AuthenticatedMember member) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(member);
    }

    @PostMapping("/token")
    public ResponseEntity<AuthService.AccessResponse> reissue(@Valid @RequestBody TokenRequest request) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").header("Pragma", "no-cache")
                .body(auth.reissue(request.refreshToken()));
    }

    public record TokenRequest(@NotBlank String refreshToken) {}
}
