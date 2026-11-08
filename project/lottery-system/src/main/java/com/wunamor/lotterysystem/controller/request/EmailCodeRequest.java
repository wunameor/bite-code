package com.wunamor.lotterysystem.controller.request;

import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取邮箱验证码请求：注册 / 登录 / 重置密码共用一个接口，用 scene 区分
 */
@Data
public class EmailCodeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用场景：REGISTER / LOGIN / RESET_PASSWORD
     */
    @NotNull(message = "验证码场景不能为空")
    private EmailSceneEnum scene;

    /**
     * 账号标识：用户名或邮箱。
     * <ul>
     *     <li>REGISTER：必须是邮箱，验证码直接发到这个地址</li>
     *     <li>LOGIN / RESET_PASSWORD：用户名或邮箱，后端查出对应邮箱再发</li>
     * </ul>
     */
    @NotBlank(message = "账号不能为空")
    private String account;
}
