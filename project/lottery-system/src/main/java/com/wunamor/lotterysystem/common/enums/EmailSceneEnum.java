package com.wunamor.lotterysystem.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件场景：用于区分不同用途的验证码，避免共用同一个 Redis key。
 * <p>
 * 放在 common 下是因为 {@code RedisKeyUtil} 需要用它拼 key，
 * common 不能反过来依赖 service。
 */
@Getter
@AllArgsConstructor
public enum EmailSceneEnum {

    REGISTER("register", "注册"),
    LOGIN("login", "登录"),
    RESET_PASSWORD("reset-password", "重置密码"),
    UPDATE_PROFILE("update-profile", "修改资料"),
    ;

    /**
     * 拼进 Redis key 的场景标识
     */
    private final String code;

    private final String desc;
}
