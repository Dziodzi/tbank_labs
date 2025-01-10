package io.github.dziodzi.exception;

import io.github.dziodzi.entity.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void handleCurrencyNotFoundException_ShouldNotFountRequest() {
        NotFoundException exception = new NotFoundException("Currency not found");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCurrencyNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Currency Not Found", body.getError());
        assertEquals("Currency not found", body.getMessage());
        assertEquals("CURRENCY_NOT_FOUND", body.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
    }

    @Test
    public void handleCurrencyConversionException_ShouldReturnBadRequest() {
        ConvertingException exception = new ConvertingException("Conversion error");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCurrencyConversionException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Currency Conversion Error", body.getError());
        assertEquals("Conversion error", body.getMessage());
        assertEquals("CURRENCY_CONVERSION_ERROR", body.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }

    @Test
    public void handleServiceUnavailableException_ShouldReturnServiceUnavailable() {
        ServiceUnavailableException exception = new ServiceUnavailableException("Service temporarily unavailable");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleServiceUnavailableException(exception);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Service Unavailable", body.getError());
        assertEquals("Service temporarily unavailable", body.getMessage());
        assertEquals("SERVICE_UNAVAILABLE", body.getErrorCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), body.getStatus());
    }

    @Test
    public void handleNullPointerException_ShouldReturnBadRequest() {
        NullPointerException exception = new NullPointerException("Null pointer error");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNullPointerException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Null Pointer Exception", body.getError());
        assertEquals("Null pointer error", body.getMessage());
        assertEquals("NULL_POINTER", body.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }

    @Test
    public void handleGeneralException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("General error");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGeneralException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Internal Server Error", body.getError());
        assertEquals("General error An internal error occurred. Please try again later.", body.getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", body.getErrorCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
    }
}
