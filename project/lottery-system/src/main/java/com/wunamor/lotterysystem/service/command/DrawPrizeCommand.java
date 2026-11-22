package com.wunamor.lotterysystem.service.command;

import lombok.Data;

import java.util.List;

/**
 * 抽奖命令：Service 层入参。
 */
@Data
public class DrawPrizeCommand {

    /**
     * 异步任务 id；同步直接调用时可为空
     */
    private Long requestId;

    private Long activityId;

    private Long prizeId;

    private List<Long> winnerIds;
}
