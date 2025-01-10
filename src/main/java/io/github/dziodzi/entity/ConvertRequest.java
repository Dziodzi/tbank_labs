package io.github.dziodzi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertRequest {

    @NotBlank(message = "Cannot be blank")
    @Size(min = 3, max = 3, message = "Must be exactly 3 characters")
    @Schema(example = "USD")
    private String fromCurrency;

    @NotBlank(message = "Cannot be blank")
    @Size(min = 3, max = 3, message = "Must be exactly 3 characters")
    @Schema(example = "EUR")
    private String toCurrency;

    @NotNull(message = "Cannot be null")
    @Positive(message = "Must be greater than 0")
    @Schema(example = "100")
    private Double amount;
}
