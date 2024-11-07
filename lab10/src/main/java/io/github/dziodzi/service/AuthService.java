package io.github.dziodzi.service;

import io.github.dziodzi.entity.Role;
import io.github.dziodzi.entity.User;
import io.github.dziodzi.entity.exchange.AuthResponse;
import io.github.dziodzi.entity.exchange.ResetPasswordRequest;
import io.github.dziodzi.entity.exchange.SignInRequest;
import io.github.dziodzi.entity.exchange.SignUpRequest;
import io.github.dziodzi.tools.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication-related operations.
 * Provides methods for user registration, login, and password reset.
 * Utilizes JWT tokens for user authentication.
 */
@Service
@RequiredArgsConstructor
@LogExecutionTime
public class AuthService {
    
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Registers a new user and returns an authentication response with a JWT token.
     *
     * @param request the sign-up request containing user credentials and information
     * @return the authentication response containing a JWT token for the new user
     */
    public AuthResponse signUp(SignUpRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userService.create(user);
        return new AuthResponse(jwtService.generateToken(user, false));
    }
    
    /**
     * Authenticates a user and returns an authentication response with a JWT token.
     *
     * @param request the sign-in request containing username and password
     * @return the authentication response containing a JWT token
     */
    public AuthResponse signIn(SignInRequest request) {
        authenticateUser(request.getUsername(), request.getPassword());
        var user = userService.userDetailsService().loadUserByUsername(request.getUsername());
        return new AuthResponse(jwtService.generateToken(user, request.isRememberMe()));
    }
    
    /**
     * Resets a user's password and returns a new authentication response with a JWT token.
     *
     * @param request the reset password request containing username, new password, and confirmation code
     * @return the authentication response containing a JWT token with updated password information
     */
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        validateConfirmationCode(request.getConfirmationCode());
        var user = userService.getByUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.update(user);
        UserDetails userDetails = userService.getByUsername(request.getUsername());
        return new AuthResponse(jwtService.generateToken(userDetails, true));
    }
    
    /**
     * Authenticates a user by verifying their username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    private void authenticateUser(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
    
    /**
     * Validates the confirmation code for password reset.
     *
     * @param code the confirmation code to validate
     * @throws IllegalArgumentException if the code is invalid
     */
    private void validateConfirmationCode(String code) {
        if (!"0000".equals(code)) {
            throw new IllegalArgumentException("Invalid confirmation code");
        }
    }
}
