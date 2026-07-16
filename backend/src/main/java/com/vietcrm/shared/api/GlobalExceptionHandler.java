package com.vietcrm.shared.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(IllegalArgumentException exception) {
        return ApiError.of("RESOURCE_NOT_FOUND", exception.getMessage(), traceId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException exception) {
        return new ApiError(
            "VALIDATION_FAILED",
            "Request validation failed",
            exception.getFieldErrors().stream()
                .map(error -> new ApiError.FieldErrorDetail(error.getField(), "INVALID", error.getDefaultMessage()))
                .toList(),
            traceId()
        );
    }

    private static String traceId() {
        return UUID.randomUUID().toString();
    }
}
