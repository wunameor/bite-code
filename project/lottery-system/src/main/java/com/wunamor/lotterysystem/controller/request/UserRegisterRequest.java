package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "用户类型不能为空")
    private Integer identity;

    /**
     * 邮箱验证码：由 /user/email-code 下发，5 分钟内有效且单次可用
     */
    @NotBlank(message = "邮箱验证码不能为空")
    @Pattern(regexp = "\\d{6}", message = "验证码必须是 6 位数字")
    private String emailCode;
}
