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
     * 该奖品是否已经抽满；抽满后前端才能进入下一奖品
     */
    private boolean prizeFinished;

    /**
     * 本次抽奖后该奖品还剩多少中奖名额
     */
    private int remainingCount;

    /**
     * 本次抽奖后活动是否已全部抽完
     */
    private boolean activityFinished;
}
