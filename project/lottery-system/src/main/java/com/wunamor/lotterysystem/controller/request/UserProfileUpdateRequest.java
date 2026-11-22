package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改当前登录用户资料请求。
 *
 * <p>敏感字段允许传脱敏值，Service 层会把“空值或带 * 的值”视为不修改。</p>
 */
@Data
public class UserProfileUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名 / 昵称
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 255, message = "用户名不能超过 255 个字符")
    private String userName;

    /**
     * 新邮箱；为空或带 * 表示不修改
     */
    private String email;

    /**
     * 新邮箱验证码；修改邮箱时必填
     */
    @Pattern(regexp = "^$|\\d{6}", message = "验证码必须是 6 位数字")
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
