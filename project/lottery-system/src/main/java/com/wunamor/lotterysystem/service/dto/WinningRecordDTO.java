package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * 中奖记录查询结果。
 */
@Data
public class WinningRecordDTO {

    private Long activityId;

    private Long prizeId;

    private String prizeName;

    private String prizeTier;

    private String prizeTierName;

    private Long winnerId;

    private String winnerName;

    private Date winningTime;
}
