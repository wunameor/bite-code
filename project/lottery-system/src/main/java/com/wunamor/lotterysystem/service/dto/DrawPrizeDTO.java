package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 抽奖结果。
 */
@Data
public class DrawPrizeDTO {

    private Long activityId;

    private Long prizeId;

    private int winnerCount;

    /**
     * 本次抽奖后活动是否已全部抽完
     */
    private boolean activityFinished;
}
