package com.bharat.common.exception;

public class UnauthorizedException extends BaseException{

    protected UnauthorizedException(String message) {
        super(message, 401);
    }
}
