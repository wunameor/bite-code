package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 活动列表项响应。
 */
@Data
public class ActivityListResponse {

    private Long activityId;

    private String activityName;

    private String description;

    /**
     * 是否进行中：true 可去抽奖，false 查看中奖名单
     */
    private boolean valid;
}
