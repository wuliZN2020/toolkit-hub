package com.toolkit.hub.common.exception;

import com.toolkit.hub.common.result.Result;
import com.toolkit.hub.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler
 *
 * @author zhangna
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle business exception
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("Business exception: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * Handle validation exception
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<?> handleValidationException(Exception e) {
        log.error("Validation exception: {}", e.getMessage());
        String message = "Parameter validation failed";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            if (ex.getBindingResult().hasErrors() && ex.getBindingResult().getFieldError() != null) {
                message = ex.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * Handle general exception
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("System exception: ", e);
        return Result.error(ResultCode.ERROR.getCode(), "System error: " + e.getMessage());
    }
}
