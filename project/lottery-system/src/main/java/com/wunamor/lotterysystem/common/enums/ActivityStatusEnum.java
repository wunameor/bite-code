package com.wunamor.lotterysystem.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动状态：创建后为进行中，全部奖品抽完后改为已完成。
 */
@Getter
@AllArgsConstructor
public enum ActivityStatusEnum {

    RUNNING("RUNNING", "进行中"),
    FINISHED("FINISHED", "已完成"),
    ;

    private final String code;
    private final String desc;

    /**
     * 根据数据库中的状态值查枚举，未知值返回 null。
     */
    public static ActivityStatusEnum getByCode(String code) {
        for (ActivityStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
