package com.wunamor.lotterysystem.service.mq.producer;

import com.wunamor.lotterysystem.service.mq.message.DrawCommandMessage;
import com.wunamor.lotterysystem.service.mq.message.DrawNotifyMessage;

/**
 * 抽奖消息生产者：统一投递抽奖命令和中奖通知。
 */
public interface DrawMessageProducer {

    /**
     * 投递一条抽奖命令。
     *
     * @param message 抽奖命令消息
     */
    void sendCommand(DrawCommandMessage message);

    /**
     * 投递一条中奖通知。
     *
     * @param message 中奖通知消息
     */
    void sendNotify(DrawNotifyMessage message);
}
