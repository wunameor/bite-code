package com.wunamor.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserIdentityEnum {
    ADMIN(1, "管理员"),
    NORMAL_USER(2, "普通用户"),
    ;

    private final int id;
    private final String identity;

    public static UserIdentityEnum getValueById(int id) {
        for (UserIdentityEnum userIdentityEnum : UserIdentityEnum.values()) {
            if (userIdentityEnum.id == id) return userIdentityEnum;
        }
        return null;
    }
}
