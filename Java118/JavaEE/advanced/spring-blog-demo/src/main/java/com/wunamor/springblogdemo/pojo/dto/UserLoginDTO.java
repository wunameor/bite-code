package com.wunamor.springblogdemo.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserLoginDTO {
    @NotNull(message = "用户名不能为空")
    @Length(max = 20, message = "用户名长度不能超过20")
    private String userName;

    @NotNull(message = "密码不能为空")
    @Length(max = 20, message = "密码长度不能超过20")
    private String password;
}
