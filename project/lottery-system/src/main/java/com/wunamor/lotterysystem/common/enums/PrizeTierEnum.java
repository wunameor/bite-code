package com.wunamor.lotterysystem.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 奖品等级：前端传 FIRST_PRIZE / SECOND_PRIZE / THIRD_PRIZE。
 */
@Getter
@AllArgsConstructor
public enum PrizeTierEnum {

    FIRST_PRIZE("FIRST_PRIZE", "一等奖"),
    SECOND_PRIZE("SECOND_PRIZE", "二等奖"),
    THIRD_PRIZE("THIRD_PRIZE", "三等奖"),
    ;

    private final String code;
    private final String desc;

    /**
     * 根据数据库或请求中的等级值查枚举，未知值返回 null。
     */
    public static PrizeTierEnum getByCode(String code) {
        for (PrizeTierEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
