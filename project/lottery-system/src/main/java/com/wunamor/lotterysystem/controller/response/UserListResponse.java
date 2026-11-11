package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 人员列表响应
 */
@Data
public class UserListResponse {

    private Long userId;

    private String userName;

    private Integer identity;

    private String identityDesc;
}
