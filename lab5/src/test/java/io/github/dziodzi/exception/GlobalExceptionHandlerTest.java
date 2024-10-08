package io.github.dziodzi.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundResponse() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<String> response = handler.handleResourceNotFoundException(exception);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnConflictResponse() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");

        ResponseEntity<String> response = handler.handleIllegalArgumentException(exception);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Illegal argument", response.getBody());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Some unexpected error");

        ResponseEntity<String> response = handler.handleGenericException(exception);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("An unexpected error occurred: Some unexpected error", response.getBody());
    }
}
