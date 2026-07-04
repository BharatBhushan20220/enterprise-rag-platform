package com.bharat.common.exception;

public abstract class BaseException extends RuntimeException {

    private final int statusCode;

    protected BaseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
