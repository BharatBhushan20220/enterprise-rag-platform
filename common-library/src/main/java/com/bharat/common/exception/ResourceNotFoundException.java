package com.bharat.common.exception;

public class ResourceNotFoundException extends BaseException{


    protected ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
