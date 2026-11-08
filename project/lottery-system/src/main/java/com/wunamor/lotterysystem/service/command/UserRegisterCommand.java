package com.wunamor.lotterysystem.service.command;

import lombok.Data;

/**
 * 注册命令：Service 层入参
 */
@Data
public class UserRegisterCommand {

    private String userName;

    private String email;

    private String phoneNumber;

    private String password;

    private Integer identity;

    /**
     * 邮箱验证码：由 /user/email-code 下发，5 分钟内有效且单次可用
     */
    private String emailCode;
}
