package com.rbac.common.exception;

public class BadRequestException extends RuntimeException {
    private final int code;

    public BadRequestException(String message) {
        super(message);
        this.code = 400;
    }

    public BadRequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}