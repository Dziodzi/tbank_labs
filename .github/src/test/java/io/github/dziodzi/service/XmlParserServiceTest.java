package io.github.dziodzi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

public class XmlParserServiceTest {

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

    private final String invalidXML = "<Valute ID=\"R01625\">\n" +
            "<NumCode>702</NumCode>";

    @InjectMocks
    private XmlParserService parser;

    @BeforeEach
    void setUp() {
        parser = new XmlParserService();
    }

    @Test
    void getValueOfCode_PositiveCase_ShouldReturnCorrectValue() {
        String result = parser.getValueOfCode("XDR", validXML);
        assertEquals("38,4205", result, "Expected value for XDR should match the parsed value.");
    }

    @Test
    void getValueOfCode_NegativeCase_ShouldReturnNullIfCodeNotFound() {
        String result = parser.getValueOfCode("RUB", validXML);
        assertNull(result, "Expected result should be null when the currency code is not found.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR", "GBP"})
    void getValueOfCode_ExceptionCase_ShouldThrowRuntimeExceptionOnInvalidXML(String currencyCode) {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            parser.getValueOfCode(currencyCode, invalidXML);
        });
        assertNotNull(exception.getMessage(), "Exception message should not be null.");
    }
}
