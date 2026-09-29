package com.mikeliwei.aistumanage.common;

import lombok.Getter;

/**
 * 业务异常，由全局异常处理器转换为统一的 Result 结构。
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务状态码，默认 400 */
    private final Integer code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
