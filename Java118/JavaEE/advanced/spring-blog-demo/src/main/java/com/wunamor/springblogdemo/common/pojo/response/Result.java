package com.wunamor.springblogdemo.common.pojo.response;


import com.wunamor.springblogdemo.common.enums.ResultCodeEnums;
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

    public Result(ResultCodeEnums resultCodeEnums) {
        this(resultCodeEnums.getCode(), resultCodeEnums.getMsg(), null);
    }

    public Result(ResultCodeEnums resultCodeEnums, T data) {
        this(resultCodeEnums.getCode(), resultCodeEnums.getMsg(), data);
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCodeEnums.SUCCESS, data);
    }

    public static <T> Result<T> fail(ResultCodeEnums resultCodeEnums, T data) {
        return new Result<>(resultCodeEnums, data);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg);
    }

    public static <T> Result<T> fail(ResultCodeEnums resultCodeEnums) {
        return new Result<>(resultCodeEnums, null);
    }

    public static <T> Result<T> fail() {
        return new Result<>(ResultCodeEnums.FAIL, null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(ResultCodeEnums.FAIL.getCode(), msg);
    }
}
