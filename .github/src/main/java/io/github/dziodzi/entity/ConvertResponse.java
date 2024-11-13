package io.github.dziodzi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertResponse {

    @Schema(example = "USD")
    private String fromCurrency;

    @Schema(example = "EUR")
    private String toCurrency;

    @Schema(example = "100")
    private double amount;

    @Schema(example = "91.298")
    private double convertedAmount;
}
