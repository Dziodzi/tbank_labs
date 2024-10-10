package io.github.dziodzi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dziodzi.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import io.github.dziodzi.entity.ConvertRequest;
import io.github.dziodzi.exception.ServiceUnavailableException;
import io.github.dziodzi.service.CurrencyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getCurrencyExchangeRate_Success_ShouldReturnValidRate() throws Exception {
        String currencyCode = "USD";
        double rate = 100.0;
        Mockito.when(currencyService.getValueOfCurrencyByCode(currencyCode)).thenReturn(rate);

        mockMvc.perform(get("/currencies/rates/{code}", currencyCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(currencyCode))
                .andExpect(jsonPath("$.rate").value(rate));
    }

    @Test
    public void getCurrencyExchangeRate_NotFound_ShouldReturnBadRequest() throws Exception {

        String invalidCode = "INVALID";
        Mockito.when(currencyService.getValueOfCurrencyByCode(invalidCode))
                .thenThrow(new NotFoundException("Invalid currency code"));

        mockMvc.perform(get("/currencies/rates/{code}", invalidCode))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void convertCurrency_NegativeAmount_ShouldReturnBadRequest() throws Exception {
        ConvertRequest request = new ConvertRequest();
        request.setFromCurrency("USD");
        request.setToCurrency("EUR");
        request.setAmount(-100.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(currencyService, never()).convertCurrency(any());
    }

    @Test
    public void convertCurrency_ZeroAmount_ShouldReturnBadRequest() throws Exception {
        ConvertRequest request = new ConvertRequest();
        request.setFromCurrency("USD");
        request.setToCurrency("EUR");
        request.setAmount(0.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(currencyService, never()).convertCurrency(any());
    }

    @Test
    public void convertCurrency_InvalidFromCurrency_ShouldReturnBadRequest() throws Exception {
        ConvertRequest request = new ConvertRequest();
        request.setFromCurrency("ДОЛЛАР, ДОЛЛАР, ДОЛЛАР! ГРЯЗНАЯ ЗЕЛЁНАЯ БУМАЖКА!");
        request.setToCurrency("EUR");
        request.setAmount(-100.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(currencyService, never()).convertCurrency(any());
    }

    @Test
    public void convertCurrency_InvalidToCurrency_ShouldReturnBadRequest() throws Exception {
        ConvertRequest request = new ConvertRequest();
        request.setFromCurrency("USD");
        request.setToCurrency("Я ИМЕЮ ВАЛЮТУ");
        request.setAmount(100.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(currencyService, never()).convertCurrency(any());
    }

    @Test
    public void convertCurrency_Success_ShouldReturnConvertedAmount() throws Exception {
        ConvertRequest request = new ConvertRequest();
        request.setFromCurrency("USD");
        request.setToCurrency("EUR");
        request.setAmount(100.0);

        double convertedAmount = 85.00;
        Mockito.when(currencyService.convertCurrency(request)).thenReturn(convertedAmount);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value(request.getFromCurrency()))
                .andExpect(jsonPath("$.toCurrency").value(request.getToCurrency()))
                .andExpect(jsonPath("$.amount").value(request.getAmount()))
                .andExpect(jsonPath("$.convertedAmount").value(convertedAmount));
    }

    @Test
    void handleServiceUnavailableException_ShouldReturnServiceUnavailable() throws Exception {
        Mockito.when(currencyService.convertCurrency(any(ConvertRequest.class)))
                .thenThrow(new ServiceUnavailableException("Service is temporarily unavailable"));

        ConvertRequest request = new ConvertRequest();
        request.setFromCurrency("USD");
        request.setToCurrency("EUR");
        request.setAmount(100.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Retry-After", "3600"))
                .andExpect(jsonPath("$.message").value("Service is temporarily unavailable"))
                .andExpect(jsonPath("$.status").value(HttpStatus.SERVICE_UNAVAILABLE.value()));
    }
}
