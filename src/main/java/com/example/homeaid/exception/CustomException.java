package com.example.homeaid.exception;

import com.example.homeaid.exception.errorcode.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final BaseErrorCode errorCode;

    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = getErrorCode();
    }
}
