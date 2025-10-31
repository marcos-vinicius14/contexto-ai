package com.contextoia.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.contextoia.common.dto.ErrorResponse;
import com.contextoia.common.exceptions.InvalidDataException;
import com.contextoia.common.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        logger.error("Resource not found: {}", ex.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getErrorCode())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .build();
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDataException(
            InvalidDataException ex,
            HttpServletRequest request) {

        logger.error("Invalid data: {}", ex.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errorCode(ex.getErrorCode())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected error", ex);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Erro interno do servidor")
                .errorCode("INTERNAL_ERROR")
                .path(request.getRequestURI())
                .build();
    }

}
