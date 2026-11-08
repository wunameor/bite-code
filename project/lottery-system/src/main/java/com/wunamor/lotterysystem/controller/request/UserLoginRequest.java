package com.wunamor.lotterysystem.controller.request;

import com.wunamor.lotterysystem.common.enums.LoginTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求：两种登录方式共用一个接口，用 type 区分
 * <p>
 * account 既可以是用户名，也可以是邮箱：含 @ 按邮箱查，否则按用户名查。
 * <p>
 * 注意：password 和 emailCode 不能加 @NotBlank，
 * 否则另一种登录方式永远校验不过，必填校验在 Service 里按 type 分支处理。
 */
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录方式：PASSWORD / EMAIL_CODE
     */
    @NotNull(message = "登录方式不能为空")
    private LoginTypeEnum type;

    /**
     * 账号标识：用户名或邮箱
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * type = PASSWORD 时必填
     */
    private String password;

    /**
     * type = EMAIL_CODE 时必填
     */
    private String emailCode;
}
