package com.wunamor.springblogdemo.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCodeEnums {
    SUCCESS(2000, "请求成功"),
    FAIL(3000, "服务器内部错误，请联系管理员"),


    USER_NO_LOGIN(3100, "用户未登录，请登录"),
    USER_USER_NAME_ERROR(3101, "用户名不正确"),
    USER_PASSWORD_ERROR(3102, "密码不正确"),
    ;
    private final int code;
    private final String msg;

}
