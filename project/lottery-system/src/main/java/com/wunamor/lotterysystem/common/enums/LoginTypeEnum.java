package com.wunamor.lotterysystem.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录方式：请求体里的 type 字段，由 Service 用 switch 分发到不同实现。
 * <p>
 * 出现在接口契约里（请求字段 + 服务分发），因此放在 common/enums。
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    PASSWORD("密码登录"),
    EMAIL_CODE("邮箱验证码登录"),
    ;

    private final String desc;
}
