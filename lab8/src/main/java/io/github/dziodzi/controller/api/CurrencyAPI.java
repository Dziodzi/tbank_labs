package io.github.dziodzi.controller.api;

import io.github.dziodzi.entity.ConvertRequest;
import io.github.dziodzi.entity.ConvertResponse;
import io.github.dziodzi.entity.RateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface CurrencyAPI {

    @Operation(summary = "Get currency rate", description = "Returns the current currency rate by code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved currency rate",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"currency\": \"USD\", \"rate\": 97.2394}"))),
            @ApiResponse(responseCode = "400", description = "Invalid currency code or bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"timestamp\": \"2024-10-11T12:06:49.711883629\",\n" +
                                    "  \"error\": \"Currency Conversion Error\",\n" +
                                    "  \"message\": \"Currency not found by code: USDЫ\",\n" +
                                    "  \"errorCode\": \"CURRENCY_CONVERSION_ERROR\",\n" +
                                    "  \"status\": 400\n" +
                                    "}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"timestamp\": \"2024-10-11T12:07:56.665679932\",\n" +
                                    "  \"error\": \"Internal Server Error\",\n" +
                                    "  \"message\": \"JSON parse error: Unexpected character ('h' (code 104)): was expecting comma to separate Object entries An internal error occurred. Please try again later.\",\n" +
                                    "  \"errorCode\": \"INTERNAL_SERVER_ERROR\",\n" +
                                    "  \"status\": 500\n" +
                                    "}")))
    })
    ResponseEntity<RateResponse> getCurrencyExchangeRate(
            @Parameter(description = "Currency code (e.g., USD, EUR)", example = "USD") String code);

    @Operation(summary = "Convert currency", description = "Converts one currency to another based on current rates")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully converted currency",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fromCurrency\": \"USD\", \"toCurrency\": \"EUR\", \"amount\": 100, \"convertedAmount\": 91.298}"))),
            @ApiResponse(responseCode = "400", description = "Invalid conversion request or bad data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"timestamp\": \"2024-10-11T12:06:49.711883629\",\n" +
                                    "  \"error\": \"Currency Conversion Error\",\n" +
                                    "  \"message\": \"Currency not found by code: USDЫ\",\n" +
                                    "  \"errorCode\": \"CURRENCY_CONVERSION_ERROR\",\n" +
                                    "  \"status\": 400\n" +
                                    "}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"timestamp\": \"2024-10-11T12:07:56.665679932\",\n" +
                                    "  \"error\": \"Internal Server Error\",\n" +
                                    "  \"message\": \"JSON parse error: Unexpected character ('h' (code 104)): was expecting comma to separate Object entries An internal error occurred. Please try again later.\",\n" +
                                    "  \"errorCode\": \"INTERNAL_SERVER_ERROR\",\n" +
                                    "  \"status\": 500\n" +
                                    "}")))
    })
    ResponseEntity<ConvertResponse> convertCurrency(ConvertRequest request);
}
