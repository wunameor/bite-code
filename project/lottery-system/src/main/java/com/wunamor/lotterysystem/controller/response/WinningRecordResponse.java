package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

import java.util.Date;

/**
 * 中奖记录响应。
 */
@Data
public class WinningRecordResponse {

    private Long activityId;

    private Long prizeId;

    private String prizeName;

    private String prizeTier;

    private String prizeTierName;

    private Long winnerId;

    private String winnerName;

    private Date winningTime;
}
