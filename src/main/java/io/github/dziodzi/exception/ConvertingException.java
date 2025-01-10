package io.github.dziodzi.exception;

import lombok.Getter;

@Getter
public class ConvertingException extends RuntimeException {

    public ConvertingException(String message) {
        super(message);
    }
}

