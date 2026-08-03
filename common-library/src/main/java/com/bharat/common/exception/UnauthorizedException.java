package com.bharat.common.exception;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, 401, cause);
    }
}
