package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 登录结果：两种登录方式返回结构一致
 */
@Data
public class UserLoginDTO {

    private String token;

    private Long userId;

    private String userName;

    private Integer identity;
}
