package com.k955.kdex.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode code;

    public ResourceNotFoundException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

}
