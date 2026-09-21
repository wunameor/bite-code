package com.wunamor.bookdemo.model;

import com.wunamor.bookdemo.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> ok(int code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static <T> Result<T> ok(T data) {
        return ok(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> ok() {
        return ok(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), null);
    }

    public static <T> Result<T> fail() {
        return fail(ResultCodeEnum.FAIL.getCode(), ResultCodeEnum.FAIL.getMsg(), null);
    }

    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum, T data) {
        return fail(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), data);
    }

    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum) {
        return fail(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), null);
    }

    public static <T> Result<T> fail(int code, String msg, T data) {
        return new Result<>(code, msg, data);
    }
}
