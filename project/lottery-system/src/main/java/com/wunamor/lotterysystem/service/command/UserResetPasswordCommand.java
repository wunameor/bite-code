package com.wunamor.lotterysystem.service.command;

import lombok.Data;

/**
 * 邮箱验证码重置密码命令。
 */
@Data
public class UserResetPasswordCommand {

    /**
     * 用户名或邮箱
     */
    private String account;

    /**
     * 重置密码场景的邮箱验证码
     */
    private String emailCode;

    /**
     * 新密码
     */
    private String newPassword;
}
