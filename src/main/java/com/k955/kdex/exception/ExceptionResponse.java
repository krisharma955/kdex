package com.k955.kdex.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ExceptionResponse(
        HttpStatus status,
        ErrorCode code,
        String message,
        String path,
        Instant timestamp
) {
}
