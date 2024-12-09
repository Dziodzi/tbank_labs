package io.github.dziodzi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dziodzi.entity.exchange.ResetPasswordRequest;
import io.github.dziodzi.entity.exchange.SignInRequest;
import io.github.dziodzi.entity.exchange.SignUpRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @Order(1)
    void signUp_ShouldCreateNewUser() throws Exception {
        SignUpRequest request = new SignUpRequest("testUser", "test@example.com", "password123");
        
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
    
    @Test
    @Order(2)
    void signIn_ShouldAuthenticateUser() throws Exception {
        SignInRequest request = new SignInRequest("testUser", "password123", true);
        
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    
    @Test
    @Order(3)
    void resetPassword_ShouldSendResetLink() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("test@example.com", "newPassword", "0000");
        
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    @Order(4)
    void adminIndex_ShouldReturnNotEnoughRights() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is(403));
    }
}
