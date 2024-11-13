package io.github.dziodzi.service;

import io.github.dziodzi.exception.NotFoundException;
import io.github.dziodzi.tools.LogExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@LogExecutionTime
public class CurrencyParserService {
    private XmlParserService parse;

    @Autowired
    public void setParse(XmlParserService parse) {
        this.parse = parse;
    }

    public String getCurrencyValueByCode(String code, String xmlData) throws NotFoundException {
        String result = parse.getValueOfCode(code, xmlData);
        if (result == null || result.isBlank()) {
            throw new NotFoundException("Currency not found by code: " + code);
        }

        return result;
    }
}
