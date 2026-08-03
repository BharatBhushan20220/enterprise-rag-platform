package com.bharat.common.exception;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, 403);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, 403, cause);
    }
}
