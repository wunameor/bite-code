package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 当前登录用户资料响应。
 */
@Data
public class UserProfileResponse {

    private Long userId;

    private String userName;

    /**
     * 脱敏后的邮箱
     */
    private String emailMasked;

    /**
     * 脱敏后的手机号
     */
    private String phoneMasked;

    private Integer identity;

    private String identityDesc;
}
