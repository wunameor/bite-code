package com.wunamor.lotterysystem.service.command;

import com.wunamor.lotterysystem.common.enums.LoginTypeEnum;
import lombok.Data;

/**
 * 登录命令：Service 层入参。
 * <p>
 * 由 Controller 从 {@code UserLoginRequest} 转换而来，
 * 这样 Service 不再依赖 Controller 层的请求对象。
 */
@Data
public class UserLoginCommand {

    /**
     * 登录方式：PASSWORD / EMAIL_CODE
     */
    private LoginTypeEnum type;

    /**
     * 账号标识：用户名或邮箱
     */
    private String account;

    /**
     * type = PASSWORD 时使用
     */
    private String password;

    /**
     * type = EMAIL_CODE 时使用
     */
    private String emailCode;
}
