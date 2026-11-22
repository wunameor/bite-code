package com.wunamor.lotterysystem.service.mq.consumer;

import com.wunamor.lotterysystem.common.constants.RabbitMqConstants;
import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.service.DrawRequestService;
import com.wunamor.lotterysystem.service.mq.message.DrawCommandMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 抽奖命令消费者：只负责消息解析和委托，抽奖编排由应用服务完成。
 */
@Slf4j
@Component
public class DrawCommandListener {

    private final DrawRequestService drawRequestService;

    public DrawCommandListener(DrawRequestService drawRequestService) {
        this.drawRequestService = drawRequestService;
    }

    /**
     * 消费抽奖命令并交给任务服务处理。
     *
     * @param json 抽奖命令 JSON
     */
    @RabbitListener(queues = RabbitMqConstants.DRAW_COMMAND_QUEUE)
    public void onMessage(String json) {
        // 1. 解析消息；格式错误直接进入死信队列
        DrawCommandMessage message = parseMessage(json);
        if (message == null || message.getRequestId() == null) {
            log.error("抽奖命令缺少任务编号，无法处理");
            throw new AmqpRejectAndDontRequeueException("抽奖命令缺少任务编号");
        }

        // 2. 交给应用服务完成任务抢占、抽奖和状态回写
        drawRequestService.handleCommand(message);
    }

    private DrawCommandMessage parseMessage(String json) {
        try {
            return JacksonUtil.readValue(json, DrawCommandMessage.class);
        } catch (RuntimeException e) {
            log.error("抽奖命令解析失败，messageLength: {}", json == null ? 0 : json.length(), e);
            throw new AmqpRejectAndDontRequeueException("抽奖命令解析失败", e);
        }
    }
}
