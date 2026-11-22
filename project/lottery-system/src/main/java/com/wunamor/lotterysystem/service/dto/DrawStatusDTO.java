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

    private boolean activityFinished;

    private Long nextPrizeId;

    private String nextPrizeTier;

    private String nextPrizeTierName;

    private Integer errorCode;

    private String errorMessage;
}
