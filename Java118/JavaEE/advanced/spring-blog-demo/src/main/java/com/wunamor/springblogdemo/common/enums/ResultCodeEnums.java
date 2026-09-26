package com.wunamor.springblogdemo.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCodeEnums {
    SUCCESS(2000, "请求成功"),
    FAIL(3000, "服务器内部错误，请联系管理员"),


    USER_NO_LOGIN(3100, "用户未登录，请登录"),
    ;
    private final int code;
    private final String msg;

}
