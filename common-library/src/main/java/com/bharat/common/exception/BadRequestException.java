package com.bharat.common.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, 400, cause);
    }
}
