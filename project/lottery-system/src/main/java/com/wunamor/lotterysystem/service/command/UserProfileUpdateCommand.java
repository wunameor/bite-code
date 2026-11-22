package com.wunamor.lotterysystem.service.command;

import lombok.Data;

/**
 * 修改当前登录用户资料命令。
 *
 * <p>email、phoneNumber 为空或仍是脱敏值时表示不修改；password 相关字段为空时表示不修改密码。</p>
 */
@Data
public class UserProfileUpdateCommand {

    /**
     * 当前登录用户 id，由 Controller 从 JWT 注入，前端不能指定
     */
    private Long userId;

    /**
     * 用户名 / 昵称
     */
    private String userName;

    /**
     * 新邮箱；为空或带 * 表示不修改
     */
    private String email;

    /**
     * 新邮箱验证码；修改邮箱时必填
     */
    private String emailCode;

    /**
     * 新手机号；为空或带 * 表示不修改
     */
    private String phoneNumber;

    /**
     * 原密码；修改密码时必填
     */
    private String oldPassword;

    /**
     * 新密码；为空表示不修改密码
     */
    private String newPassword;
}
