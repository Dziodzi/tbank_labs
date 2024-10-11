package io.github.dziodzi.service;

import io.github.dziodzi.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CurrencyParserServiceTest {

    private final String validXML = """
            <ValCurs Date="02.03.2002" name="Foreign Currency Market">
                <Valute ID="R01589">
                    <NumCode>960</NumCode>
                    <CharCode>XDR</CharCode>
                    <Nominal>1</Nominal>
                    <Name>СДР (специальные права заимствования)</Name>
                    <Value>38,4205</Value>
                    <VunitRate>38,4205</VunitRate>
                </Valute>
            </ValCurs>
            """;

    @InjectMocks
    private CurrencyParserService currencyParserService;

    @Mock
    private XmlParserService parser; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCurrencyValueByCode_PositiveCase_ShouldReturnCurrencyValue() throws Exception {
        String code = "XDR";
        String expectedValue = "38,4205";
        when(parser.getValueOfCode(code, validXML)).thenReturn(expectedValue);
        String result = currencyParserService.getCurrencyValueByCode(code, validXML);
        assertEquals(expectedValue, result, "Expected value for currency code AUD should match.");
    }

    @Test
    void getCurrencyValueByCode_NegativeCase_ShouldThrowNotFoundException() {
        String code = "RUB";
        when(parser.getValueOfCode(code, validXML)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> currencyParserService.getCurrencyValueByCode(code, validXML));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            currencyParserService.getCurrencyValueByCode(code, validXML);
        });
        assertEquals("Currency not found by code: RUB", exception.getMessage(), "Expected exception message for not found currency code DKK.");
    }

    @Test
    void getCurrencyValueByCode_NegativeCase_ShouldThrowNotFoundExceptionForBlankResult() {
        String code = "BYR";
        when(parser.getValueOfCode(code, validXML)).thenReturn("  ");
        assertThrows(NotFoundException.class, () -> currencyParserService.getCurrencyValueByCode(code, validXML));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            currencyParserService.getCurrencyValueByCode(code, validXML);
        });
        assertEquals("Currency not found by code: BYR", exception.getMessage(), "Expected exception message for blank currency value.");
    }
}
