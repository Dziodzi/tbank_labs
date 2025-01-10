package io.github.dziodzi.controller;

import io.github.dziodzi.entity.exchange.AuthResponse;
import io.github.dziodzi.entity.exchange.ResetPasswordRequest;
import io.github.dziodzi.entity.exchange.SignInRequest;
import io.github.dziodzi.entity.exchange.SignUpRequest;
import io.github.dziodzi.service.AuthService;
import io.github.dziodzi.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthController class.
 * This class tests the AuthController's methods for user sign-up, sign-in, logout, and password reset functionalities.
 */
class AuthControllerTest {
    
    @Mock
    private AuthService authService;
    
    @Mock
    private LogoutService logoutService;
    
    @Mock
    private HttpServletRequest httpServletRequest;
    
    @InjectMocks
    private AuthController authController;
    
    /**
     * Initializes the mock objects before each test method.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * Test case for valid sign-up request.
     * Verifies that when a valid sign-up request is made, an AuthResponse is returned.
     */
    @Test
    void whenValidSignUpRequest_thenReturnAuthResponse() {
        SignUpRequest request = new SignUpRequest("newUser", "user@example.com", "securePassword123");
        AuthResponse expectedResponse = new AuthResponse("testToken123");
        when(authService.signUp(any(SignUpRequest.class))).thenReturn(expectedResponse);
        
        AuthResponse actualResponse = authController.signUp(request);
        
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(authService, times(1)).signUp(request);
    }
    
    /**
     * Test case for invalid sign-up request.
     * Verifies that when an invalid sign-up request is made, an exception is thrown.
     */
    @Test
    void whenInvalidSignUpRequest_thenThrowException() {
        SignUpRequest invalidRequest = new SignUpRequest("", "invalidEmail", "");
        when(authService.signUp(any(SignUpRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));
        
        assertThrows(IllegalArgumentException.class, () -> authController.signUp(invalidRequest));
        verify(authService, times(1)).signUp(invalidRequest);
    }
    
    /**
     * Test case for valid sign-in request.
     * Verifies that when a valid sign-in request is made, an AuthResponse is returned.
     */
    @Test
    void whenValidSignInRequest_thenReturnAuthResponse() {
        SignInRequest request = new SignInRequest("existingUser", "validPassword", true);
        AuthResponse expectedResponse = new AuthResponse("validToken123");
        when(authService.signIn(any(SignInRequest.class))).thenReturn(expectedResponse);
        
        AuthResponse actualResponse = authController.signIn(request);
        
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(authService, times(1)).signIn(request);
    }
    
    /**
     * Test case for invalid sign-in credentials.
     * Verifies that when invalid credentials are provided, an exception is thrown.
     */
    @Test
    void whenInvalidCredentials_thenThrowException() {
        SignInRequest invalidRequest = new SignInRequest("user", "wrongPassword", false);
        when(authService.signIn(any(SignInRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));
        
        assertThrows(IllegalArgumentException.class, () -> authController.signIn(invalidRequest));
        verify(authService, times(1)).signIn(invalidRequest);
    }
    
    /**
     * Test case for logging out with a valid token.
     * Verifies that when a valid token is provided, the token is invalidated and a success message is returned.
     */
    @Test
    void whenLogoutWithValidToken_thenInvalidateTokenAndReturnSuccessMessage() {
        String token = "validToken123";
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        
        ResponseEntity<?> response = authController.logout(httpServletRequest);
        
        assertEquals(ResponseEntity.ok("You have successfully logged out"), response);
        verify(logoutService, times(1)).invalidateToken(token);
    }
    
    /**
     * Test case for a valid password reset request.
     * Verifies that when a valid reset password request is made, the password is reset,
     * and a valid AuthResponse is returned along with a successful token invalidation.
     */
    @Test
    void whenValidResetPasswordRequest_thenResetPasswordAndReturnAuthResponse() {
        ResetPasswordRequest request = new ResetPasswordRequest("existingUser", "0000", "newSecurePassword123");
        AuthResponse expectedResponse = new AuthResponse("resetToken123");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(authService.resetPassword(request)).thenReturn(expectedResponse);
        
        ResponseEntity<AuthResponse> response = authController.resetPassword(request, httpServletRequest);
        
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(authService, times(1)).resetPassword(request);
        verify(logoutService, times(1)).invalidateToken("token");
    }
    
    /**
     * Test case for an invalid confirmation code during password reset.
     * Verifies that when an invalid confirmation code is provided, an exception is thrown.
     */
    @Test
    void whenInvalidConfirmationCode_thenThrowException() {
        ResetPasswordRequest request = new ResetPasswordRequest("user", "invalidCode", "newPassword123");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(authService.resetPassword(request)).thenThrow(new IllegalArgumentException("Invalid confirmation code"));
        
        assertThrows(IllegalArgumentException.class, () -> authController.resetPassword(request, httpServletRequest));
        verify(authService, times(1)).resetPassword(request);
    }
    
    /**
     * Test case for password reset with an invalid token.
     * Verifies that when the reset password request is made with no authorization token,
     * the response is null and no actions are performed.
     */
    @Test
    void whenResetPasswordWithInvalidToken_thenReturnNullResponse() {
        ResetPasswordRequest request = new ResetPasswordRequest("user", "0000", "newSecurePassword");
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);
        
        ResponseEntity<AuthResponse> response = authController.resetPassword(request, httpServletRequest);
        
        assertEquals(ResponseEntity.ok(null), response);
        verify(authService, never()).resetPassword(request);
        verify(logoutService, never()).invalidateToken(anyString());
    }
    
    /**
     * Test case for checking successful user registration with correct data.
     * Verifies that when valid registration data is provided, a valid response is returned.
     */
    @Test
    void whenValidSignUpRequest_thenReturnCorrectResponse() {
        SignUpRequest request = new SignUpRequest("newUser", "newuser@example.com", "validPassword123");
        AuthResponse expectedResponse = new AuthResponse("validTokenForNewUser");
        when(authService.signUp(any(SignUpRequest.class))).thenReturn(expectedResponse);
        
        AuthResponse actualResponse = authController.signUp(request);
        
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getJwtToken(), actualResponse.getJwtToken());
        verify(authService, times(1)).signUp(request);
    }
    
    /**
     * Test case for failure during reset password due to internal service exception.
     * Verifies that an internal error in the password reset service will be handled correctly.
     */
    @Test
    void whenResetPasswordFails_thenThrowInternalError() {
        ResetPasswordRequest request = new ResetPasswordRequest("existingUser", "0000", "newPassword123");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(authService.resetPassword(request)).thenThrow(new RuntimeException("Internal service error"));
        
        assertThrows(RuntimeException.class, () -> authController.resetPassword(request, httpServletRequest));
        verify(authService, times(1)).resetPassword(request);
        verify(logoutService, never()).invalidateToken("token");
    }
}
