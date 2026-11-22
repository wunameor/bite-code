package com.wunamor.lotterysystem.service.mq.message;

import lombok.Data;

/**
 * 抽奖通知消息：一个中奖人一条消息。
 */
@Data
public class DrawNotifyMessage {

    private Long activityId;

    private String activityName;

    private Long prizeId;

    private String prizeName;

    private String prizeTier;

    private String prizeTierName;

    private Long winnerId;

    private String winnerName;

    private String winnerEmail;

    private String winningTime;
}
