package com.wunamor.lotterysystem.service.mq.message;

import lombok.Data;

import java.util.List;

/**
 * 抽奖命令消息：由前台请求投递，消费者执行真正的抽奖逻辑。
 */
@Data
public class DrawCommandMessage {

    /**
     * 抽奖任务 id，同时作为 requestId
     */
    private Long requestId;

    private Long activityId;

    private Long prizeId;

    private List<Long> winnerIds;
}
