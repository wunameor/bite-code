package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class UserLoginResponse {

    private String token;

    private Long userId;

    private String userName;

    private Integer identity;
}
