package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

import java.util.List;

/**
 * 抽奖任务状态响应。
 */
@Data
public class DrawStatusResponse {

    private Long requestId;

    private Long activityId;

    private Long prizeId;

    private String status;

    private String prizeTier;

    private String prizeTierName;

    private List<DrawWinnerResponse> winnerList;

    private boolean activityFinished;

    private Long nextPrizeId;

    private String nextPrizeTier;

    private String nextPrizeTierName;

    private Integer errorCode;

    private String errorMessage;
}
