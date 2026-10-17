package com.k955.kdex.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception, HttpServletRequest request
    ) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.NOT_FOUND,
                exception.getCode(),
                exception.getMessage(),
                request.getServletPath(),
                Instant.now());
        log.warn("Resource not found: code={}, path={}", exception.getCode(), request.getServletPath());
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(
            BadRequestException exception, HttpServletRequest request
    ) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                exception.getCode(),
                exception.getMessage(),
                request.getServletPath(),
                Instant.now());
        log.error("Bad Request: code={}, path={}", exception.getCode(), request.getServletPath());
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(
            AccessDeniedException exception, HttpServletRequest request
    ) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.FORBIDDEN,
                exception.getCode(),
                exception.getMessage(),
                request.getServletPath(),
                Instant.now());
        log.error("Bad Request: code={}, path={}", exception.getCode(), request.getServletPath());
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ExceptionResponse> handleOptimisticLockFailure(
            Exception ex,
            HttpServletRequest request
    ) {
        log.warn("Concurrent modification detected for path: {}", request.getRequestURI());

        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.CONFLICT,
                ErrorCode.CONCURRENT_MODIFICATION,
                "Your account was updated by another request. Please refresh and try again.",
                request.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpServletRequest request
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Request validation failed.";
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                message,
                request.getServletPath(),
                Instant.now());
        log.warn("Validation error: path={}, message={}", request.getServletPath(), message);
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request
    ) {
        String message = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Request validation failed.";
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                message,
                request.getServletPath(),
                Instant.now());
        log.warn("Constraint violation: path={}, message={}", request.getServletPath(), message);
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST,
                "The request is malformed or contains invalid values.",
                request.getServletPath(),
                Instant.now());
        log.warn("Malformed request: path={}, type={}", request.getServletPath(), exception.getClass().getSimpleName());
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ExceptionResponse> handleMissingRequestHeader(
            MissingRequestHeaderException exception, HttpServletRequest request
    ) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST,
                "Missing required header: " + exception.getHeaderName(),
                request.getServletPath(),
                Instant.now());
        log.warn("Missing header: path={}, header={}", request.getServletPath(), exception.getHeaderName());
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception, HttpServletRequest request
    ) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_REQUEST,
                "The request conflicts with an existing record. Please try again.",
                request.getServletPath(),
                Instant.now());
        log.warn("Data integrity violation: path={}", request.getServletPath());
        return ResponseEntity.status(exceptionResponse.status()).body(exceptionResponse);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

}
