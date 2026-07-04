package com.bharat.common.exception;

public class BadRequestException extends BaseException{

    protected BadRequestException(String message) {
        super(message, 400);
    }
}
