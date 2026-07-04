package com.bharat.common.exception;

public class ConflictException extends BaseException{

    protected ConflictException(String message) {
        super(message, 409);
    }
}
