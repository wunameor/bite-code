package com.wunamor.rabbitextensions.constants;

public class Constant {
    // 消息确认
    public static final String ACK_QUEUE = "ack.queue";
    public static final String ACK_EXCHANGE = "ack.exchange";

    // 持久化
    public static final String DURABLE_QUEUE = "durable.queue";
    public static final String DURABLE_EXCHANGE = "durable.exchange";

    // 发送方确认
    public static final String CONFIRM_QUEUE = "confirm.queue";
    public static final String CONFIRM_EXCHANGE = "confirm.exchange";

    // 重试机制
    public static final String RETRY_QUEUE = "retry.queue";
    public static final String RETRY_EXCHANGE = "retry.exchange";

    // 过期时间 ttl
    public static final String TTL_QUEUE = "ttl.queue";
    public static final String NO_TTL_QUEUE = "no.ttl.queue";
    public static final String TTL_EXCHANGE = "ttl.exchange";

    // 死信
    public static final String NORMAL_QUEUE = "normal.queue";
    public static final String NORMAL_EXCHANGE = "normal.exchange";
    public static final String DL_QUEUE = "dl.queue";
    public static final String DL_EXCHANGE = "dl.exchange";

    // 延迟队列
    public static final String DELAY_QUEUE = "delay.queue";
    public static final String DELAY_EXCHANGE = "delay.exchange";
}
