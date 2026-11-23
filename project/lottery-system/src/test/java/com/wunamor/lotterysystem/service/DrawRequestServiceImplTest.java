package com.wunamor.lotterysystem.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wunamor.lotterysystem.common.config.DrawCompensationProperties;
import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.ActivityUserMapper;
import com.wunamor.lotterysystem.mapper.DrawRequestMapper;
import com.wunamor.lotterysystem.mapper.WinningRecordMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.mapper.dataobject.DrawRequestDO;
import com.wunamor.lotterysystem.service.command.DrawPrizeCommand;
import com.wunamor.lotterysystem.service.dto.DrawRequestDTO;
import com.wunamor.lotterysystem.service.dto.DrawStatusDTO;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.wunamor.lotterysystem.service.enums.DrawRequestStatusEnum;
import com.wunamor.lotterysystem.service.impl.DrawRequestServiceImpl;
import com.wunamor.lotterysystem.service.mq.message.DrawCommandMessage;
import com.wunamor.lotterysystem.service.mq.producer.DrawMessageProducer;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 抽奖异步任务服务测试。
 */
class DrawRequestServiceImplTest {

    private DrawRequestMapper drawRequestMapper;
    private DrawMessageProducer drawMessageProducer;
    private DrawPrizeService drawPrizeService;
    private ActivityPrizeMapper activityPrizeMapper;
    private ActivityUserMapper activityUserMapper;
    private WinningRecordMapper winningRecordMapper;
    private DrawCompensationProperties compensationProperties;
    private DrawRequestServiceImpl service;

    @BeforeAll
    static void initTableInfo() {
        MapperBuilderAssistant assistant =
                new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, DrawRequestDO.class);
        TableInfoHelper.initTableInfo(assistant, ActivityPrizeDO.class);
        TableInfoHelper.initTableInfo(assistant, ActivityUserDO.class);
    }

    @BeforeEach
    void setUp() {
        drawRequestMapper = mock(DrawRequestMapper.class);
        drawMessageProducer = mock(DrawMessageProducer.class);
        drawPrizeService = mock(DrawPrizeService.class);
        activityPrizeMapper = mock(ActivityPrizeMapper.class);
        activityUserMapper = mock(ActivityUserMapper.class);
        winningRecordMapper = mock(WinningRecordMapper.class);
        compensationProperties = mock(DrawCompensationProperties.class);
        service = new DrawRequestServiceImpl(drawRequestMapper, drawMessageProducer,
                drawPrizeService, activityPrizeMapper, activityUserMapper,
                winningRecordMapper, compensationProperties);
    }

    @Test
    void submitInsertsPendingTaskAndPublishesMessage() {
        when(activityPrizeMapper.selectOne(any())).thenReturn(
                activityPrize(10L, PrizeTierEnum.FIRST_PRIZE,
                        ActivityPrizeStatusEnum.PENDING));
        when(drawRequestMapper.insert(any(DrawRequestDO.class))).thenAnswer(invocation -> {
            DrawRequestDO request = invocation.getArgument(0);
            request.setId(99L);
            return 1;
        });

        DrawRequestDTO result = service.submit(command());

        assertEquals(99L, result.getRequestId());
        assertEquals(DrawRequestStatusEnum.PENDING.getCode(), result.getStatus());
        DrawCommandMessage message = captureMessage();
        assertEquals(99L, message.getRequestId());
        assertEquals(100L, message.getActivityId());
        assertEquals(10L, message.getPrizeId());
        assertEquals(List.of(1L, 2L), message.getWinnerIds());
    }

    @Test
    void submitKeepsTaskPendingWhenPublishFails() {
        when(activityPrizeMapper.selectOne(any())).thenReturn(
                activityPrize(10L, PrizeTierEnum.FIRST_PRIZE,
                        ActivityPrizeStatusEnum.PENDING));
        when(drawRequestMapper.insert(any(DrawRequestDO.class))).thenAnswer(invocation -> {
            DrawRequestDO request = invocation.getArgument(0);
            request.setId(99L);
            return 1;
        });
        doThrow(new RuntimeException("mq down"))
                .when(drawMessageProducer).sendCommand(any());

        DrawRequestDTO result = assertDoesNotThrow(() -> service.submit(command()));

        assertEquals(DrawRequestStatusEnum.PENDING.getCode(), result.getStatus());
        verify(drawRequestMapper).insert(any(DrawRequestDO.class));
    }

    @Test
    void submitRejectsWhenPrizeCapacityIsExhausted() {
        ActivityPrizeDO prize = activityPrize(10L, PrizeTierEnum.FIRST_PRIZE,
                ActivityPrizeStatusEnum.PENDING);
        prize.setPrizeAmount(2L);
        when(activityPrizeMapper.selectOne(any())).thenReturn(prize);
        when(winningRecordMapper.countByActivityIdAndPrizeId(100L, 10L))
                .thenReturn(2L);
        when(drawRequestMapper.selectList(any())).thenReturn(List.of());

        ServiceException e = assertThrows(ServiceException.class,
                () -> service.submit(command()));

        assertEquals(1063, e.getCode());
        verify(drawRequestMapper, never()).insert(any(DrawRequestDO.class));
        verifyNoInteractions(drawMessageProducer);
    }

    @Test
    void submitAllowsAnotherTaskForSamePrizeWhenSlotsRemain() {
        ActivityPrizeDO prize = activityPrize(10L, PrizeTierEnum.FIRST_PRIZE,
                ActivityPrizeStatusEnum.PENDING);
        prize.setPrizeAmount(5L);
        when(activityPrizeMapper.selectOne(any())).thenReturn(prize);
        when(winningRecordMapper.countByActivityIdAndPrizeId(100L, 10L))
                .thenReturn(0L);
        // 同奖品已有一个未完成任务占用 1 个名额，仍然允许继续提交
        when(drawRequestMapper.selectList(any())).thenReturn(List.of(
                request(DrawRequestStatusEnum.PENDING, null)));
        when(drawRequestMapper.insert(any(DrawRequestDO.class))).thenAnswer(invocation -> {
            DrawRequestDO request = invocation.getArgument(0);
            request.setId(99L);
            return 1;
        });

        DrawRequestDTO result = assertDoesNotThrow(() -> service.submit(command()));

        assertEquals(99L, result.getRequestId());
        verify(drawMessageProducer).sendCommand(any());
    }

    @Test
    void handleCommandDrawsPrizeAfterClaimingTask() {
        when(drawRequestMapper.update(any(), any())).thenReturn(1);

        service.handleCommand(commandMessage());

        verify(drawPrizeService).drawPrize(argThat(command ->
                command.getRequestId().equals(1L)
                        && command.getActivityId().equals(100L)
                        && command.getPrizeId().equals(10L)
                        && command.getWinnerIds().equals(List.of(1L, 2L))));
    }

    @Test
    void handleCommandIgnoresDuplicateMessage() {
        when(drawRequestMapper.update(any(), any())).thenReturn(0);
        when(drawRequestMapper.selectById(1L)).thenReturn(null);

        service.handleCommand(commandMessage());

        verify(drawPrizeService, never()).drawPrize(any());
    }

    @Test
    void handleCommandMarksBusinessFailureAndAcknowledges() {
        DrawRequestServiceImpl spyService = spy(service);
        when(drawRequestMapper.update(any(), any())).thenReturn(1);
        doThrow(new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ORDER_INVALID))
                .when(drawPrizeService).drawPrize(any());

        assertDoesNotThrow(() -> spyService.handleCommand(commandMessage()));

        verify(spyService).markFailed(1L, 1056, "抽奖顺序错误，请先抽取当前奖品");
    }

    @Test
    void handleCommandRethrowsTechnicalFailureForSpringRetry() {
        DrawRequestServiceImpl spyService = spy(service);
        when(drawRequestMapper.update(any(), any())).thenReturn(1);
        doThrow(new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ERROR))
                .when(drawPrizeService).drawPrize(any());

        assertThrows(ServiceException.class,
                () -> spyService.handleCommand(commandMessage()));

        verify(spyService, never()).markFailed(any(), anyInt(), anyString());
    }

    @Test
    void markProcessingReturnsTrueWhenTaskClaimed() {
        when(drawRequestMapper.update(any(), any())).thenReturn(1);

        assertTrue(service.markProcessing(1L));
    }

    @Test
    void markProcessingReturnsFalseWhenTaskAlreadyHandled() {
        when(drawRequestMapper.update(any(), any())).thenReturn(0);
        when(drawRequestMapper.selectById(1L)).thenReturn(null);

        assertFalse(service.markProcessing(1L));
    }

    @Test
    void markProcessingAllowsSpringAmqpRetryOnProcessingTask() {
        DrawRequestDO request = request(DrawRequestStatusEnum.PROCESSING, null);
        when(drawRequestMapper.update(any(), any())).thenReturn(0);
        when(drawRequestMapper.selectById(1L)).thenReturn(request);

        assertTrue(service.markProcessing(1L));
    }

    @Test
    void getStatusThrowsWhenRequestNotFound() {
        when(drawRequestMapper.selectById(1L)).thenReturn(null);

        ServiceException e = assertThrows(ServiceException.class,
                () -> service.getStatus(1L));

        assertEquals(1055, e.getCode());
    }

    @Test
    void getStatusReturnsResultSnapshotAndNextPrize() {
        when(drawRequestMapper.selectById(1L)).thenReturn(request(
                DrawRequestStatusEnum.SUCCESS, "[{\"userId\":1,\"userName\":\"张三\"}]"));
        when(activityPrizeMapper.selectOne(any())).thenReturn(activityPrize(10L,
                PrizeTierEnum.FIRST_PRIZE, ActivityPrizeStatusEnum.DRAWN));
        when(activityPrizeMapper.selectCount(any())).thenReturn(1L);
        when(activityPrizeMapper.selectList(any())).thenReturn(List.of(
                activityPrize(11L, PrizeTierEnum.SECOND_PRIZE,
                        ActivityPrizeStatusEnum.PENDING)));

        DrawStatusDTO status = service.getStatus(1L);

        assertEquals(DrawRequestStatusEnum.SUCCESS.getCode(), status.getStatus());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getDesc(), status.getPrizeTierName());
        assertEquals("张三", status.getWinnerList().get(0).getUserName());
        assertFalse(status.isActivityFinished());
        assertEquals(11L, status.getNextPrizeId());
        assertEquals(PrizeTierEnum.SECOND_PRIZE.getDesc(),
                status.getNextPrizeTierName());
    }

    @Test
    void getStatusFallsBackToActivityUsersBeforeSuccess() {
        when(drawRequestMapper.selectById(1L)).thenReturn(request(
                DrawRequestStatusEnum.PENDING, null));
        when(activityPrizeMapper.selectOne(any())).thenReturn(activityPrize(10L,
                PrizeTierEnum.FIRST_PRIZE, ActivityPrizeStatusEnum.PENDING));
        when(activityUserMapper.selectList(any())).thenReturn(List.of(
                activityUser(1L, "张三")));
        when(activityPrizeMapper.selectCount(any())).thenReturn(1L);
        when(activityPrizeMapper.selectList(any())).thenReturn(List.of(
                activityPrize(10L, PrizeTierEnum.FIRST_PRIZE,
                        ActivityPrizeStatusEnum.PENDING)));

        DrawStatusDTO status = service.getStatus(1L);

        assertEquals("张三", status.getWinnerList().get(0).getUserName());
        assertEquals(DrawRequestStatusEnum.PENDING.getCode(), status.getStatus());
    }

    @Test
    void compensateRepublishesPendingTask() {
        when(compensationProperties.isEnabled()).thenReturn(true);
        when(compensationProperties.getMaxAttempts()).thenReturn(3);
        when(compensationProperties.getPendingDelay()).thenReturn(Duration.ofSeconds(1));
        when(compensationProperties.getProcessingTimeout()).thenReturn(Duration.ofSeconds(1));
        DrawRequestDO task = request(DrawRequestStatusEnum.PENDING, null);
        when(drawRequestMapper.selectList(any()))
                .thenReturn(List.of(task), List.of());
        when(drawRequestMapper.update(any(), any())).thenReturn(1);

        service.compensate();

        verify(drawMessageProducer).sendCommand(any());
    }

    @Test
    void compensateDoesNothingWhenDisabled() {
        when(compensationProperties.isEnabled()).thenReturn(false);

        service.compensate();

        verifyNoInteractions(drawRequestMapper);
    }

    private DrawPrizeCommand command() {
        DrawPrizeCommand command = new DrawPrizeCommand();
        command.setActivityId(100L);
        command.setPrizeId(10L);
        command.setWinnerIds(List.of(1L, 2L));
        return command;
    }

    private DrawCommandMessage commandMessage() {
        DrawCommandMessage message = new DrawCommandMessage();
        message.setRequestId(1L);
        message.setActivityId(100L);
        message.setPrizeId(10L);
        message.setWinnerIds(List.of(1L, 2L));
        return message;
    }

    private DrawRequestDO request(DrawRequestStatusEnum status, String resultJson) {
        DrawRequestDO request = new DrawRequestDO();
        request.setId(1L);
        request.setActivityId(100L);
        request.setPrizeId(10L);
        request.setWinnerIds("[1]");
        request.setStatus(status.getCode());
        request.setRetryCount(0);
        request.setResultJson(resultJson);
        return request;
    }

    private ActivityPrizeDO activityPrize(Long prizeId, PrizeTierEnum tier,
                                         ActivityPrizeStatusEnum status) {
        ActivityPrizeDO activityPrize = new ActivityPrizeDO();
        activityPrize.setId(prizeId);
        activityPrize.setActivityId(100L);
        activityPrize.setPrizeId(prizeId);
        activityPrize.setPrizeAmount(5L);
        activityPrize.setPrizeTiers(tier.getCode());
        activityPrize.setStatus(status.getCode());
        return activityPrize;
    }

    private ActivityUserDO activityUser(Long userId, String userName) {
        ActivityUserDO user = new ActivityUserDO();
        user.setActivityId(100L);
        user.setUserId(userId);
        user.setUserName(userName);
        return user;
    }

    @SuppressWarnings("unchecked")
    private DrawCommandMessage captureMessage() {
        org.mockito.ArgumentCaptor<DrawCommandMessage> captor =
                org.mockito.ArgumentCaptor.forClass(DrawCommandMessage.class);
        verify(drawMessageProducer).sendCommand(captor.capture());
        return captor.getValue();
    }
}
