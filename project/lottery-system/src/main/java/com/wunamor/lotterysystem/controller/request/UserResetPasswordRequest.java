package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱验证码重置密码请求。
 */
@Data
public class UserResetPasswordRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名或邮箱
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 重置密码验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "\\d{6}", message = "验证码必须是 6 位数字")
    private String emailCode;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
