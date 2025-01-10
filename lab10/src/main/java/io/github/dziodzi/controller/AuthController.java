package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.AuthAPI;
import io.github.dziodzi.entity.exchange.AuthResponse;
import io.github.dziodzi.entity.exchange.ResetPasswordRequest;
import io.github.dziodzi.entity.exchange.SignInRequest;
import io.github.dziodzi.entity.exchange.SignUpRequest;
import io.github.dziodzi.service.AuthService;
import io.github.dziodzi.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AuthController implements AuthAPI {
    private final AuthService authService;
    private final LogoutService logoutService;

    @Override
    public AuthResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authService.signUp(request);
    }

    @Override
    public AuthResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authService.signIn(request);
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Logging out token: " + token);
            logoutService.invalidateToken(token);
        }
        return ResponseEntity.ok("You have successfully logged out");
    }

    @Override
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        AuthResponse response = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Resetting password for token: " + token);
            response = authService.resetPassword(request);
            logoutService.invalidateToken(token);
        }
        return ResponseEntity.ok(response);
    }
}
