package com.wunamor.lotterysystem.service.mq.producer;

import com.wunamor.lotterysystem.common.constants.RabbitMqConstants;
import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.service.mq.message.DrawCommandMessage;
import com.wunamor.lotterysystem.service.mq.message.DrawNotifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 抽奖消息生产者实现：消息体统一使用 JSON 字符串。
 */
@Slf4j
@Component
public class DrawMessageProducerImpl implements DrawMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public DrawMessageProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendCommand(DrawCommandMessage message) {
        // 1. 空消息没有投递意义
        if (message == null || message.getRequestId() == null) {
            return;
        }

        // 2. 序列化后投递到抽奖命令队列
        String json = JacksonUtil.writeObjectAsString(message);
        rabbitTemplate.convertAndSend(RabbitMqConstants.DRAW_EXCHANGE,
                RabbitMqConstants.DRAW_COMMAND_ROUTING_KEY, json);
        log.info("抽奖命令已投递，requestId: {}, activityId: {}, prizeId: {}",
                message.getRequestId(), message.getActivityId(), message.getPrizeId());
    }

    @Override
    public void sendNotify(DrawNotifyMessage message) {
        // 1. 空消息没有投递意义
        if (message == null) {
            return;
        }

        // 2. 序列化后投递到抽奖通知交换机
        String json = JacksonUtil.writeObjectAsString(message);
        rabbitTemplate.convertAndSend(RabbitMqConstants.DRAW_EXCHANGE,
                RabbitMqConstants.DRAW_ROUTING_KEY, json);
        log.info("抽奖通知已投递，activityId: {}, winnerId: {}",
                message.getActivityId(), message.getWinnerId());
    }
}
