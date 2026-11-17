package com.wunamor.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动奖品状态：创建时为待抽取，抽奖完成后改为已抽取。
 */
@Getter
@AllArgsConstructor
public enum ActivityPrizeStatusEnum {

    PENDING("PENDING", "待抽取"),
    DRAWN("DRAWN", "已抽取"),
    ;

    private final String code;
    private final String desc;

    public static ActivityPrizeStatusEnum getByCode(String code) {
        for (ActivityPrizeStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
