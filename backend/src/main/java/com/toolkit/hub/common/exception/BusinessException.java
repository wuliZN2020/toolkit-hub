package com.toolkit.hub.common.exception;

import com.toolkit.hub.common.result.ResultCode;
import lombok.Getter;

/**
 * Business exception
 *
 * @author zhangna
 */
@Getter
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}
