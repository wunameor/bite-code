package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 活动详情中的参与人快照。
 */
@Data
public class ActivityDetailUserDTO {

    private Long userId;

    /**
     * 活动创建时保存的用户名快照，不随用户后续改名变化
     */
    private String userName;
}
