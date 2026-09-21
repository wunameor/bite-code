package com.wunamor.bookdemo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultCodeEnum {
    SUCCESS(200, "请求成功"),
    NO_LOGIN(-1, "用户未登录"),
    FAIL(-2, "请求失败"),
    ;
    private final int code;
    private final String msg;
}
