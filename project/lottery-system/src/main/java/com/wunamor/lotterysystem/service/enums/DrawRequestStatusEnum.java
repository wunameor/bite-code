package com.wunamor.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽奖异步任务状态。
 */
@Getter
@AllArgsConstructor
public enum DrawRequestStatusEnum {

    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    ;

    private final String code;
    private final String desc;

    /**
     * 根据状态 code 查枚举，未知值返回 null。
     */
    public static DrawRequestStatusEnum getByCode(String code) {
        for (DrawRequestStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
