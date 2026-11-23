package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

import java.util.List;

/**
 * 抽奖任务状态查询结果。
 */
@Data
public class DrawStatusDTO {

    private Long requestId;

    private Long activityId;

    private Long prizeId;

    private String status;

    private String prizeTier;

    private String prizeTierName;

    private List<DrawWinnerDTO> winnerList;

    /**
     * 当前奖品是否已经抽满
     */
    private boolean prizeFinished;

    /**
     * 当前奖品还剩多少中奖名额
     */
    private int prizeRemaining;

    private boolean activityFinished;

    private Long nextPrizeId;

    private String nextPrizeTier;

    private String nextPrizeTierName;

    private Integer errorCode;

    private String errorMessage;
}
