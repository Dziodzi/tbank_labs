package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.CurrencyAPI;
import io.github.dziodzi.entity.ConvertRequest;
import io.github.dziodzi.entity.ConvertResponse;
import io.github.dziodzi.entity.RateResponse;
import io.github.dziodzi.service.CurrencyService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currencies")
@Log4j2
public class CurrencyController implements CurrencyAPI {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    @GetMapping("/rates/{code}")
    public ResponseEntity<RateResponse> getCurrencyExchangeRate(
            @Valid @PathVariable("code") String code) {

        double currencyValue = currencyService.getValueOfCurrencyByCode(code);
        RateResponse response = new RateResponse(code, currencyValue);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> convertCurrency(
            @Valid @RequestBody ConvertRequest request) {

        double convertedAmount = currencyService.convertCurrency(request);
        ConvertResponse response = new ConvertResponse(
                request.getFromCurrency(),
                request.getToCurrency(),
                request.getAmount(),
                convertedAmount
        );

        return ResponseEntity.ok(response);
    }
}
