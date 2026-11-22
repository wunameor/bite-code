package com.wunamor.lotterysystem.service.mq.consumer;

import com.wunamor.lotterysystem.common.constants.RabbitMqConstants;
import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.common.utils.MailUtil;
import com.wunamor.lotterysystem.service.mq.message.DrawNotifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

/**
 * 抽奖通知消费者：异步发送中奖邮件，失败时进入死信队列。
 */
@Slf4j
@Component
public class DrawNotifyListener {

    private final MailUtil mailUtil;

    public DrawNotifyListener(MailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    /**
     * 消费中奖通知并发送邮件。
     *
     * @param json 中奖通知 JSON
     */
    @RabbitListener(queues = RabbitMqConstants.DRAW_NOTIFY_QUEUE)
    public void onMessage(String json) {
        try {
            // 1. 反序列化通知消息
            DrawNotifyMessage message = JacksonUtil.readValue(json, DrawNotifyMessage.class);

            // 2. 发送 HTML 中奖邮件
            mailUtil.sendHtml(message.getWinnerEmail(),
                    "恭喜您中奖啦", buildHtml(message));
            log.info("中奖邮件发送完成，activityId: {}, winnerId: {}",
                    message.getActivityId(), message.getWinnerId());
        } catch (RuntimeException e) {
            // 3. 邮件或消息处理失败时不重回队列，交给死信队列留痕
            // 日志不打印完整消息，避免邮箱等个人信息落盘
            log.error("抽奖通知处理失败，messageLength: {}",
                    json == null ? 0 : json.length(), e);
            throw new AmqpRejectAndDontRequeueException("抽奖通知处理失败", e);
        }
    }

    /**
     * 构建中奖邮件正文，所有业务字段做 HTML 转义。
     */
    private String buildHtml(DrawNotifyMessage message) {
        return "<div style=\"font-family:Arial,sans-serif;line-height:1.8;\">"
                + "<h2>恭喜您中奖啦！</h2>"
                + "<p>" + HtmlUtils.htmlEscape(message.getWinnerName())
                + "，您在活动「" + HtmlUtils.htmlEscape(message.getActivityName())
                + "」中获得了：</p>"
                + "<p style=\"font-size:18px;font-weight:bold;\">"
                + HtmlUtils.htmlEscape(message.getPrizeTierName()) + " "
                + HtmlUtils.htmlEscape(message.getPrizeName()) + "</p>"
                + "<p>中奖时间：" + HtmlUtils.htmlEscape(message.getWinningTime()) + "</p>"
                + "</div>";
    }
}
