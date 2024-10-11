package io.github.dziodzi.service;

import io.github.dziodzi.entity.ConvertRequest;
import io.github.dziodzi.exception.ConvertingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyServiceTest {

    @Mock
    private ApiService apiService;

    @Mock
    private CurrencyParserService parserService;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getValueOfCurrencyByCode_ValidCode_ReturnsValue() throws Exception {
        String currencyCode = "EUR"; // Changed to Euro
        String expectedCurrencyData = "<ValCurs>...</ValCurs>"; // Mocked currency data
        String expectedValue = "82.5"; // Changed value
        double expected = 82.5;

        when(apiService.fetchCurrencyDataWithCode(currencyCode)).thenReturn(expectedCurrencyData);
        when(parserService.getCurrencyValueByCode(currencyCode, expectedCurrencyData)).thenReturn(expectedValue);

        double actual = currencyService.getValueOfCurrencyByCode(currencyCode);

        assertEquals(expected, actual);
        verify(apiService).fetchCurrencyDataWithCode(currencyCode);
        verify(parserService).getCurrencyValueByCode(currencyCode, expectedCurrencyData);
    }

    @Test
    void getValueOfCurrencyByCode_InvalidCode_ThrowsConvertingException() throws Exception {
        String currencyCode = "INVALID_CODE";
        when(apiService.fetchCurrencyDataWithCode(currencyCode)).thenThrow(new Exception("Invalid currency code"));

        ConvertingException exception = assertThrows(ConvertingException.class, () -> {
            currencyService.getValueOfCurrencyByCode(currencyCode);
        });

        assertEquals("Invalid currency code", exception.getMessage());
        verify(apiService).fetchCurrencyDataWithCode(currencyCode);
    }

    @Test
    void convertCurrency_ValidRequest_ReturnsConvertedAmount() throws Exception {
        ConvertRequest request = new ConvertRequest("EUR", "JPY", 100.0);
        String currencyData = "<ValCurs>...</ValCurs>";
        String eurValue = "82.5";
        String jpyValue = "0.75";

        when(apiService.fetchCurrencyDataWithRequest(request)).thenReturn(currencyData);
        when(parserService.getCurrencyValueByCode("EUR", currencyData)).thenReturn(eurValue);
        when(parserService.getCurrencyValueByCode("JPY", currencyData)).thenReturn(jpyValue);

        double result = currencyService.convertCurrency(request);

        assertEquals(11000.0, result);
        verify(apiService).fetchCurrencyDataWithRequest(request);
        verify(parserService).getCurrencyValueByCode("EUR", currencyData);
        verify(parserService).getCurrencyValueByCode("JPY", currencyData);
    }

    @Test
    void convertCurrency_NullFromCurrency_ThrowsConvertingException() {
        ConvertRequest request = new ConvertRequest(null, "JPY", 100.0);

        ConvertingException exception = assertThrows(ConvertingException.class, () -> {
            currencyService.convertCurrency(request);
        });

        assertEquals("Currency codes can't be null", exception.getMessage());
    }

    @Test
    void convertCurrency_NullToCurrency_ThrowsConvertingException() {
        ConvertRequest request = new ConvertRequest("EUR", null, 100.0);

        ConvertingException exception = assertThrows(ConvertingException.class, () -> {
            currencyService.convertCurrency(request);
        });

        assertEquals("Currency codes can't be null", exception.getMessage());
    }

    @Test
    void convertCurrency_PositiveCase_ShouldCalculateConversionSuccessfully() throws Exception {
        ConvertRequest request = new ConvertRequest("EUR", "GBP", 100.0);
        String currencyData = "<ValCurs>...</ValCurs>"; // Mocked currency data

        when(apiService.fetchCurrencyDataWithRequest(request)).thenReturn(currencyData);
        when(parserService.getCurrencyValueByCode("EUR", currencyData)).thenReturn("82.5");
        when(parserService.getCurrencyValueByCode("GBP", currencyData)).thenReturn("1.1");

        double result = currencyService.convertCurrency(request);

        assertEquals(7500.0, result);
        verify(apiService).fetchCurrencyDataWithRequest(request);
        verify(parserService).getCurrencyValueByCode("EUR", currencyData);
        verify(parserService).getCurrencyValueByCode("GBP", currencyData);
    }
}
