package com.wunamor.lotterysystem.service.mq.consumer;

import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.common.utils.MailUtil;
import com.wunamor.lotterysystem.service.mq.message.DrawNotifyMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DrawNotifyListenerTest {

    @Test
    void onMessageSendsHtmlMail() {
        MailUtil mailUtil = mock(MailUtil.class);
        DrawNotifyListener listener = new DrawNotifyListener(mailUtil);

        listener.onMessage(JacksonUtil.writeObjectAsString(message()));

        verify(mailUtil).sendHtml(
                org.mockito.ArgumentMatchers.eq("winner@test.com"),
                org.mockito.ArgumentMatchers.eq("恭喜您中奖啦"),
                contains("张三"));
    }

    @Test
    void onMessageRejectsWhenMailFails() {
        MailUtil mailUtil = mock(MailUtil.class);
        DrawNotifyListener listener = new DrawNotifyListener(mailUtil);
        doThrow(new RuntimeException("smtp down"))
                .when(mailUtil).sendHtml(anyString(), anyString(), anyString());

        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> listener.onMessage(JacksonUtil.writeObjectAsString(message())));
    }

    private DrawNotifyMessage message() {
        DrawNotifyMessage message = new DrawNotifyMessage();
        message.setActivityId(100L);
        message.setActivityName("年会抽奖");
        message.setPrizeId(10L);
        message.setPrizeName("手机");
        message.setPrizeTier("FIRST_PRIZE");
        message.setPrizeTierName("一等奖");
        message.setWinnerId(1L);
        message.setWinnerName("张三");
        message.setWinnerEmail("winner@test.com");
        message.setWinningTime("2026-11-18 12:00:00");
        return message;
    }
}
