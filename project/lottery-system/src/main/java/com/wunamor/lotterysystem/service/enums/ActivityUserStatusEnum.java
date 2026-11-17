package com.wunamor.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动人员状态：创建时为待中奖，抽中后改为已中奖。
 */
@Getter
@AllArgsConstructor
public enum ActivityUserStatusEnum {

    PENDING("PENDING", "待中奖"),
    WINNER("WINNER", "已中奖"),
    ;

    private final String code;
    private final String desc;

    public static ActivityUserStatusEnum getByCode(String code) {
        for (ActivityUserStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
