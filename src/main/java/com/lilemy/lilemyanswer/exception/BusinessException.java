package com.lilemy.lilemyanswer.exception;


import com.lilemy.lilemyanswer.common.ResultCode;

import java.io.Serial;

public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5915032392946288013L;
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
