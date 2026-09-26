package com.wunamor.springblogdemo.common.exception;

import com.wunamor.springblogdemo.common.enums.ResultCodeEnums;
import lombok.Getter;

@Getter
public class BlogException extends RuntimeException {
    private int code;
    private String msg;

    public BlogException(ResultCodeEnums resultCodeEnums) {
        this.code = resultCodeEnums.getCode();
        this.msg = resultCodeEnums.getMsg();
    }

    public BlogException(String msg) {
        this.msg = msg;
    }
}
