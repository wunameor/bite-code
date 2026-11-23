package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

import java.util.List;

/**
 * 活动详情快照：写入 Redis，供抽奖页面读取。
 * <p>包含奖品状态、剩余候选人员和中奖名单，抽奖后由抽奖服务重建。</p>
 */
@Data
public class ActivityDetailDTO {

    private Long activityId;

    private String activityName;

    private String description;

    /**
     * 是否进行中：true 可抽奖，false 查看中奖名单
     */
    private boolean valid;

    private List<ActivityDetailPrizeDTO> prizes;

    private List<ActivityDetailUserDTO> users;
}
