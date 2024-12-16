package com.easy.exception;

public class BusinessException extends RuntimeException {
    private Integer errorCode;

    public BusinessException() {
        super();
    }

    public BusinessException(Throwable cause) {
        super(cause.getMessage());
        this.errorCode = CommonConstants.FAIL;
    }

    public BusinessException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = CommonConstants.FAIL;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
