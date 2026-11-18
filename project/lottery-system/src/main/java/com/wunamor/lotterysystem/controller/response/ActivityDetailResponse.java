package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

import java.util.List;

/**
 * 活动详情响应，字段与抽奖页面使用结构保持一致。
 */
@Data
public class ActivityDetailResponse {

    private Long activityId;

    private String activityName;

    private String description;

    /**
     * 是否进行中：true 可抽奖，false 查看中奖名单
     */
    private boolean valid;

    private List<ActivityDetailPrizeResponse> prizes;

    private List<ActivityDetailUserResponse> users;
}
