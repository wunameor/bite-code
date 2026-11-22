package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.service.command.DrawPrizeCommand;
import com.wunamor.lotterysystem.service.dto.DrawRequestDTO;
import com.wunamor.lotterysystem.service.dto.DrawStatusDTO;
import com.wunamor.lotterysystem.service.mq.message.DrawCommandMessage;

/**
 * 抽奖异步任务服务：负责提交任务、查询状态和失败补偿。
 */
public interface DrawRequestService {

    /**
     * 提交抽奖任务并投递 RabbitMQ。
     *
     * @param command 抽奖命令
     * @return 任务 id 和初始状态
     */
    DrawRequestDTO submit(DrawPrizeCommand command);

    /**
     * 查询抽奖任务状态。
     *
     * @param requestId 任务 id
     * @return 任务状态和结果
     */
    DrawStatusDTO getStatus(Long requestId);

    /**
     * 处理一条抽奖命令：抢占任务、执行抽奖并回写任务状态。
     *
     * @param message 抽奖命令消息
     */
    void handleCommand(DrawCommandMessage message);

    /**
     * 将待处理任务抢占为处理中。
     *
     * @param requestId 任务 id
     * @return true 表示抢到任务，false 表示任务已完成或不处于待处理状态
     */
    boolean markProcessing(Long requestId);

    /**
     * 标记业务失败。
     *
     * @param requestId 任务 id
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     */
    void markFailed(Long requestId, int errorCode, String errorMessage);
}
