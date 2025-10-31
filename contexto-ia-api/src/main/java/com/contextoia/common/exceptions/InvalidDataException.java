package com.contextoia.common.exceptions;

public class InvalidDataException extends BusinessException {
    public InvalidDataException(String message) {
        super(message, "INVALID_DATA");
    }
}

