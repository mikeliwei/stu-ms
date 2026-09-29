package com.mikeliwei.aistumanage.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一接口返回结构。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /** 业务状态码，200 表示成功 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

    public static <T> Result<T> ok() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
