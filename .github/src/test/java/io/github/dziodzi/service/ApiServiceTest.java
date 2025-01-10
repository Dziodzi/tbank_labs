package io.github.dziodzi.service;

import io.github.dziodzi.entity.ConvertRequest;
import io.github.dziodzi.exception.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ApiServiceTest {

    @Value("${custom.api.currency}")
    private String currencyCodesUrl;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApiService apiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(apiService, "currencyCodesUrl", currencyCodesUrl);
    }

    @Test
    void fetchCurrencyDataWithRequest_WhenServiceIsAvailable_ShouldReturnCurrencyData() throws Exception {
        ConvertRequest request = new ConvertRequest("CAD", "JPY", 250.0);
        ResponseEntity<String> responseEntity = createResponseEntity("Currency Data", HttpStatus.OK);
        when(restTemplate.getForEntity(currencyCodesUrl, String.class)).thenReturn(responseEntity);

        String result = apiService.fetchCurrencyDataWithRequest(request);

        assertEquals("Currency Data", result, "Expected to receive currency data successfully");
    }

    @Test
    void fetchCurrencyDataWithRequest_WhenServiceIsUnavailable_ShouldThrowServiceUnavailableException() {
        ConvertRequest request = new ConvertRequest("AUD", "NZD", 150.0);
        ResponseEntity<String> responseEntity = createResponseEntity(null, HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.getForEntity(currencyCodesUrl, String.class)).thenReturn(responseEntity);

        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, () -> {
            apiService.fetchCurrencyDataWithRequest(request);
        });

        assertEquals("Currency service is currently unavailable", exception.getMessage(),
                "Expected ServiceUnavailableException with specific message");
    }

    @Test
    void fetchCurrencyDataWithCode_WhenServiceIsAvailable_ShouldReturnCurrencyDataForCode() throws Exception {
        String code = "CHF";
        ResponseEntity<String> responseEntity = createResponseEntity("Currency Data for CHF", HttpStatus.OK);
        when(restTemplate.getForEntity(currencyCodesUrl, String.class)).thenReturn(responseEntity);

        String result = apiService.fetchCurrencyDataWithCode(code);

        assertEquals("Currency Data for CHF", result, "Expected to receive currency data for CHF successfully");
    }

    @Test
    void fetchCurrencyDataWithCode_WhenCodeIsNull_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            apiService.fetchCurrencyDataWithCode(null);
        }, "Expected IllegalArgumentException for null currency code");
    }

    @Test
    void fetchCurrencyDataWithCode_WhenServiceIsUnavailable_ShouldThrowServiceUnavailableException() {
        String code = "SEK";
        ResponseEntity<String> responseEntity = createResponseEntity(null, HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.getForEntity(currencyCodesUrl, String.class)).thenReturn(responseEntity);

        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, () -> {
            apiService.fetchCurrencyDataWithCode(code);
        });

        assertEquals("Currency service is currently unavailable", exception.getMessage(),
                "Expected ServiceUnavailableException with specific message");
    }

    @Test
    void fallbackFetchCurrencyData_ShouldReturnFallbackMessage() {
        String result = apiService.fallbackFetchCurrencyData(new Exception("Some error"));
        assertEquals("Currency data is currently unavailable. Please try again later.", result,
                "Expected fallback message for currency data unavailability");
    }

    private ResponseEntity<String> createResponseEntity(String body, HttpStatus status) {
        return new ResponseEntity<>(body, status);
    }
}
