package com.k955.kdex.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {

    private final ErrorCode code;

    public AccessDeniedException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

}
