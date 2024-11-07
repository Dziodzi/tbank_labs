package io.github.dziodzi.controller.api;

import io.github.dziodzi.entity.exchange.AuthResponse;
import io.github.dziodzi.entity.exchange.ResetPasswordRequest;
import io.github.dziodzi.entity.exchange.SignInRequest;
import io.github.dziodzi.entity.exchange.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "User authentication API")
@RequestMapping("/auth")
public interface AuthAPI {

    @Operation(summary = "Sign up a new user")
    @PostMapping("/sign-up")
    @ResponseBody
    AuthResponse signUp(@RequestBody @Valid SignUpRequest request);

    @Operation(summary = "Sign in an existing user")
    @PostMapping("/sign-in")
    @ResponseBody
    AuthResponse signIn(@RequestBody @Valid SignInRequest request);

    @Operation(summary = "Log out the current user")
    @PostMapping("/logout")
    ResponseEntity<?> logout(HttpServletRequest request);

    @Operation(summary = "Reset user password")
    @PostMapping("/reset-password")
    ResponseEntity<AuthResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request, HttpServletRequest httpRequest);
}
