package io.github.dziodzi.service;

import io.github.dziodzi.entity.ConvertRequest;
import io.github.dziodzi.exception.ServiceUnavailableException;
import io.github.dziodzi.tools.LogExecutionTime;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
@LogExecutionTime
public class ApiService {
    private final RestTemplate restTemplate;

    @Value("${custom.api.currency}")
    private String currencyCodesUrl;

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "currencyDataCache", key = "#request.fromCurrency + '-' + #request.toCurrency")
    @CircuitBreaker(name = "currencyApiService", fallbackMethod = "fallbackFetchCurrencyData")
    public String fetchCurrencyDataWithRequest(ConvertRequest request) throws Exception {
        return fetchCurrencyData(currencyCodesUrl);
    }

    @Cacheable(value = "currencyDataCache", key = "#code")
    @CircuitBreaker(name = "currencyApiService", fallbackMethod = "fallbackFetchCurrencyData")
    public String fetchCurrencyDataWithCode(String code) throws Exception {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Provided currency code is null or empty!");
        }
        return fetchCurrencyData(currencyCodesUrl);
    }

    private String fetchCurrencyData(String url) throws Exception {
        var response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            throw new ServiceUnavailableException("Currency service is currently unavailable");
        } else {
            throw new Exception("Failed to retrieve currency data");
        }
    }

    public String fallbackFetchCurrencyData(Throwable throwable) {
        log.warn("Fallback invoked due to: {}", throwable.getMessage());
        return "Currency data is currently unavailable. Please try again later.";
    }
}
