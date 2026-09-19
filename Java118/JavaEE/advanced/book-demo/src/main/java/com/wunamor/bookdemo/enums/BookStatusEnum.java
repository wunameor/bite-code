package com.wunamor.bookdemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;

@Getter
@RequiredArgsConstructor
public enum BookStatusEnum {
    DELETED(0, "无效"),
    NORMAL(1, "可借阅"),
    FORBID(2, "不可借阅"),
    ;

    private final static HashMap<Integer, String> VALUES = new HashMap<>();
    static {
        Arrays.stream(BookStatusEnum.values()).forEach(elem -> {
            VALUES.put(elem.getCode(), elem.getMsg());
        });
    }


    private final int code;
    private final String msg;

    public static String getNameByCode(int code) {
        return VALUES.getOrDefault(code, null);
    }

}
