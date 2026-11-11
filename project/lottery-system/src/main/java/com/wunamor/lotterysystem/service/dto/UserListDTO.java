package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 人员列表项
 */
@Data
public class UserListDTO {

    private Long userId;

    private String userName;

    /**
     * 身份：0 管理员，1 普通用户
     */
    private Integer identity;

    /**
     * 身份中文描述，前端直接展示
     */
    private String identityDesc;
}
