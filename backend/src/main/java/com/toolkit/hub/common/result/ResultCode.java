package com.toolkit.hub.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result code enum
 *
 * @author zhangna
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "Success"),
    ERROR(500, "Server error"),
    PARAM_ERROR(400, "Parameter error"),
    NOT_FOUND(404, "Resource not found"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),

    // Business error codes
    CRAWLER_ERROR(1001, "Web crawling failed"),
    BOOK_NOT_FOUND(1002, "Book not found"),
    TEMPLATE_NOT_FOUND(1003, "Template not found");

    private final Integer code;
    private final String message;
}
