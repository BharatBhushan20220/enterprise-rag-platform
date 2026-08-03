package com.bharat.common.exception;

public class ConflictException extends BaseException {

    public ConflictException(String message) {
        super(message, 409);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, 409, cause);
    }
}
