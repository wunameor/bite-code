package com.wunamor.lotterysystem.service.command;

import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;
import lombok.Data;

/**
 * 发送邮箱验证码命令：Service 层入参
 */
@Data
public class EmailCodeCommand {

    /**
     * 使用场景：REGISTER / LOGIN / RESET_PASSWORD
     */
    private EmailSceneEnum scene;

    /**
     * 账号标识：用户名或邮箱
     */
    private String account;
}
