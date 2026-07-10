package com.bharat.common.exception;

public class ResourceNotFoundException extends BaseException{


    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
