package com.bharat.common.exception;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, 404, cause);
    }
}
