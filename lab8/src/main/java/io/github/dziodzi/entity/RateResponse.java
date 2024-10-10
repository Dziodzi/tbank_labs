package io.github.dziodzi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateResponse {

    @Schema(example = "USD")
    private String currency;

    @Schema(example = "97.2394")
    private double rate;
}
