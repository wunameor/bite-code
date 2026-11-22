package com.wunamor.lotterysystem.common.constants;

/**
 * 抽奖命令和通知相关的 RabbitMQ 名称常量。
 */
public final class RabbitMqConstants {

    private RabbitMqConstants() {
    }

    /**
     * 抽奖主交换机，抽奖命令和通知共用
     */
    public static final String DRAW_EXCHANGE = "lottery.draw.exchange";

    /**
     * 抽奖命令路由键
     */
    public static final String DRAW_COMMAND_ROUTING_KEY = "draw.command";

    /**
     * 抽奖命令主队列
     */
    public static final String DRAW_COMMAND_QUEUE = "lottery.draw.command.queue";

    /**
     * 抽奖通知路由键
     */
    public static final String DRAW_ROUTING_KEY = "draw.notify";

    /**
     * 抽奖通知主队列
     */
    public static final String DRAW_NOTIFY_QUEUE = "lottery.draw.notify.queue";

    /**
     * 抽奖死信交换机，命令和通知共用
     */
    public static final String DRAW_DLX = "lottery.draw.dlx";

    /**
     * 抽奖通知死信路由键
     */
    public static final String DRAW_DLQ_ROUTING_KEY = "draw.notify.dead";

    /**
     * 抽奖通知死信队列
     */
    public static final String DRAW_DLQ = "lottery.draw.notify.dlq";

    /**
     * 抽奖命令死信路由键
     */
    public static final String DRAW_COMMAND_DLQ_ROUTING_KEY = "draw.command.dead";

    /**
     * 抽奖命令死信队列
     */
    public static final String DRAW_COMMAND_DLQ = "lottery.draw.command.dlq";
}
