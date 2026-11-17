package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 活动列表项。
 */
@Data
public class ActivityListDTO {

    private Long activityId;

    private String activityName;

    private String description;

    /**
     * 是否进行中：true 可去抽奖，false 查看中奖名单
     */
    private boolean valid;
}
