package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 当前登录用户资料 DTO。
 *
 * <p>邮箱和手机号只返回脱敏值，避免前端回显时拿到完整敏感信息。</p>
 */
@Data
public class UserProfileDTO {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户名 / 昵称
     */
    private String userName;

    /**
     * 脱敏后的邮箱，例如 a***@qq.com
     */
    private String emailMasked;

    /**
     * 脱敏后的手机号，例如 138****0000
     */
    private String phoneMasked;

    /**
     * 身份 code：0 管理员，1 普通用户
     */
    private Integer identity;

    /**
     * 身份中文描述
     */
    private String identityDesc;
}
