package com.k955.kdex.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final ErrorCode code;

    public BadRequestException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

}
