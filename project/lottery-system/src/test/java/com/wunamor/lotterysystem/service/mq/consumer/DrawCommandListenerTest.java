package com.wunamor.lotterysystem.service.mq.consumer;

import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.service.DrawRequestService;
import com.wunamor.lotterysystem.service.mq.message.DrawCommandMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 抽奖命令消费者测试：只验证消息适配和委托。
 */
class DrawCommandListenerTest {

    private DrawRequestService drawRequestService;
    private DrawCommandListener listener;

    @BeforeEach
    void setUp() {
        drawRequestService = mock(DrawRequestService.class);
        listener = new DrawCommandListener(drawRequestService);
    }

    @Test
    void consumesCommandAndDelegatesToService() {
        listener.onMessage(messageJson());

        verify(drawRequestService).handleCommand(argThat(message ->
                message.getRequestId().equals(1L)
                        && message.getActivityId().equals(100L)
                        && message.getPrizeId().equals(10L)
                        && message.getWinnerIds().equals(List.of(1L, 2L))));
    }

    @Test
    void invalidMessageRejectsToDeadLetterQueue() {
        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> listener.onMessage("{bad json"));
    }

    @Test
    void messageWithoutRequestIdRejectsToDeadLetterQueue() {
        DrawCommandMessage message = new DrawCommandMessage();
        message.setActivityId(100L);

        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> listener.onMessage(JacksonUtil.writeObjectAsString(message)));
    }

    private String messageJson() {
        DrawCommandMessage message = new DrawCommandMessage();
        message.setRequestId(1L);
        message.setActivityId(100L);
        message.setPrizeId(10L);
        message.setWinnerIds(List.of(1L, 2L));
        return JacksonUtil.writeObjectAsString(message);
    }
}
