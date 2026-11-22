package com.wunamor.lotterysystem.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wunamor.lotterysystem.common.enums.ActivityStatusEnum;
import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityMapper;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.ActivityUserMapper;
import com.wunamor.lotterysystem.mapper.DrawRequestMapper;
import com.wunamor.lotterysystem.mapper.PrizeMapper;
import com.wunamor.lotterysystem.mapper.UserMapper;
import com.wunamor.lotterysystem.mapper.WinningRecordMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.mapper.dataobject.DrawRequestDO;
import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import com.wunamor.lotterysystem.mapper.dataobject.PrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import com.wunamor.lotterysystem.service.command.DrawPrizeCommand;
import com.wunamor.lotterysystem.service.draw.state.impl.ActivityFinishedUpdater;
import com.wunamor.lotterysystem.service.draw.state.impl.ActivityUserNotWonUpdater;
import com.wunamor.lotterysystem.service.draw.state.impl.ActivityUserWinnerUpdater;
import com.wunamor.lotterysystem.service.draw.state.impl.DrawRequestSuccessUpdater;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdaterChain;
import com.wunamor.lotterysystem.service.draw.state.impl.PrizeDrawnUpdater;
import com.wunamor.lotterysystem.service.draw.state.impl.WinningRecordUpdater;
import com.wunamor.lotterysystem.service.dto.DrawPrizeDTO;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.wunamor.lotterysystem.service.enums.ActivityUserStatusEnum;
import com.wunamor.lotterysystem.service.impl.DrawPrizeServiceImpl;
import com.wunamor.lotterysystem.service.mq.message.DrawNotifyMessage;
import com.wunamor.lotterysystem.service.mq.producer.DrawMessageProducer;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DrawPrizeServiceImplTest {

    private ActivityMapper activityMapper;
    private ActivityPrizeMapper activityPrizeMapper;
    private ActivityUserMapper activityUserMapper;
    private PrizeMapper prizeMapper;
    private UserMapper userMapper;
    private WinningRecordMapper winningRecordMapper;
    private DrawRequestMapper drawRequestMapper;
    private ActivityCacheService activityCacheService;
    private DrawMessageProducer drawMessageProducer;
    private DrawPrizeServiceImpl drawPrizeService;

    @BeforeAll
    static void initTableInfo() {
        MapperBuilderAssistant assistant =
                new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, ActivityDO.class);
        TableInfoHelper.initTableInfo(assistant, ActivityPrizeDO.class);
        TableInfoHelper.initTableInfo(assistant, ActivityUserDO.class);
        TableInfoHelper.initTableInfo(assistant, DrawRequestDO.class);
    }

    @BeforeEach
    void setUp() {
        activityMapper = mock(ActivityMapper.class);
        activityPrizeMapper = mock(ActivityPrizeMapper.class);
        activityUserMapper = mock(ActivityUserMapper.class);
        prizeMapper = mock(PrizeMapper.class);
        userMapper = mock(UserMapper.class);
        winningRecordMapper = mock(WinningRecordMapper.class);
        drawRequestMapper = mock(DrawRequestMapper.class);
        activityCacheService = mock(ActivityCacheService.class);
        drawMessageProducer = mock(DrawMessageProducer.class);
        DrawStateUpdaterChain updaterChain = new DrawStateUpdaterChain(List.of(
                new WinningRecordUpdater(winningRecordMapper),
                new PrizeDrawnUpdater(activityPrizeMapper),
                new ActivityUserWinnerUpdater(activityUserMapper),
                new ActivityFinishedUpdater(activityMapper, activityPrizeMapper),
                new ActivityUserNotWonUpdater(activityUserMapper),
                new DrawRequestSuccessUpdater(drawRequestMapper)));
        drawPrizeService = new DrawPrizeServiceImpl(activityMapper, activityPrizeMapper,
                activityUserMapper, prizeMapper, userMapper, activityCacheService,
                drawMessageProducer, updaterChain);
    }

    @Test
    @SuppressWarnings("unchecked")
    void drawPrizeWritesRecordsAndUpdatesStatuses() {
        stubSuccessfulDraw();

        DrawPrizeDTO result = drawPrizeService.drawPrize(validCommand());

        assertEquals(100L, result.getActivityId());
        assertEquals(10L, result.getPrizeId());
        assertEquals(2, result.getWinnerCount());
        assertTrue(result.isActivityFinished());

        ArgumentCaptor<List<WinningRecordDO>> captor = ArgumentCaptor.forClass(List.class);
        verify(winningRecordMapper).insertBatch(captor.capture());
        List<WinningRecordDO> records = captor.getValue();
        assertEquals(2, records.size());
        assertEquals("年会抽奖", records.get(0).getActivityName());
        assertEquals("手机", records.get(0).getPrizeName());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getCode(), records.get(0).getPrizeTier());
        assertEquals("张三", records.get(0).getWinnerName());
        assertEquals("zhangsan@test.com", records.get(0).getWinnerEmail());
        assertEquals("13800000001", records.get(0).getWinnerPhoneNumber());
        assertNotNull(records.get(0).getWinningTime());

        verify(activityPrizeMapper).update(any(), any());
        // 一次把中奖人员更新为 WINNER，一次把剩余人员收尾为 NOT_WON
        verify(activityUserMapper, times(2)).update(any(), any());
        verify(activityMapper).update(any(), any());
        verify(activityCacheService).evictActivityDetail(100L);

        ArgumentCaptor<DrawNotifyMessage> messageCaptor =
                ArgumentCaptor.forClass(DrawNotifyMessage.class);
        verify(drawMessageProducer, times(2)).sendNotify(messageCaptor.capture());
        assertEquals("zhangsan@test.com",
                messageCaptor.getAllValues().get(0).getWinnerEmail());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getDesc(),
                messageCaptor.getAllValues().get(0).getPrizeTierName());
    }

    @Test
    void drawPrizeThrowsWhenDrawingLaterPrizeFirst() {
        when(activityMapper.selectByIdForUpdate(100L))
                .thenReturn(activityDO(100L, "年会抽奖",
                        ActivityStatusEnum.RUNNING.getCode()));
        ActivityPrizeDO secondPrize = activityPrizeDO(
                2L, 11L, 1L, PrizeTierEnum.SECOND_PRIZE,
                ActivityPrizeStatusEnum.PENDING);
        ActivityPrizeDO firstPrize = activityPrizeDO(
                1L, 10L, 1L, PrizeTierEnum.FIRST_PRIZE,
                ActivityPrizeStatusEnum.PENDING);
        when(activityPrizeMapper.selectOne(any())).thenReturn(secondPrize);
        when(activityPrizeMapper.selectList(any()))
                .thenReturn(List.of(secondPrize, firstPrize));
        when(prizeMapper.selectById(11L)).thenReturn(prizeDO(11L, "耳机"));

        DrawPrizeCommand command = validCommand();
        command.setPrizeId(11L);
        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(command));

        assertEquals(1056, e.getCode());
        verify(winningRecordMapper, never()).insertBatch(any());
    }

    @Test
    void drawPrizeSendsNotificationAfterCommitWhenTransactionActive() {
        stubSuccessfulDraw();
        TransactionSynchronizationManager.initSynchronization();
        try {
            drawPrizeService.drawPrize(validCommand());

            verify(activityCacheService, never()).evictActivityDetail(any());
            verify(drawMessageProducer, never()).sendNotify(any());

            TransactionSynchronizationManager.getSynchronizations()
                    .forEach(TransactionSynchronization::afterCommit);

            verify(activityCacheService).evictActivityDetail(100L);
            verify(drawMessageProducer, times(2)).sendNotify(any());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void drawPrizeThrowsWhenActivityNotFound() {
        when(activityMapper.selectByIdForUpdate(100L)).thenReturn(null);

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1045, e.getCode());
        verify(winningRecordMapper, never()).insertBatch(any());
    }

    @Test
    void drawPrizeThrowsWhenActivityFinished() {
        when(activityMapper.selectByIdForUpdate(100L))
                .thenReturn(activityDO(100L, "年会抽奖",
                        ActivityStatusEnum.FINISHED.getCode()));

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1047, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenPrizeNotInActivity() {
        when(activityMapper.selectByIdForUpdate(100L))
                .thenReturn(activityDO(100L, "年会抽奖",
                        ActivityStatusEnum.RUNNING.getCode()));
        when(activityPrizeMapper.selectOne(any())).thenReturn(null);

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1048, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenPrizeAlreadyDrawn() {
        when(activityMapper.selectByIdForUpdate(100L))
                .thenReturn(activityDO(100L, "年会抽奖",
                        ActivityStatusEnum.RUNNING.getCode()));
        when(activityPrizeMapper.selectOne(any())).thenReturn(activityPrizeDO(
                1L, 10L, 2L, PrizeTierEnum.FIRST_PRIZE,
                ActivityPrizeStatusEnum.DRAWN));

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1049, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenWinnerCountMismatch() {
        when(activityMapper.selectByIdForUpdate(100L))
                .thenReturn(activityDO(100L, "年会抽奖",
                        ActivityStatusEnum.RUNNING.getCode()));
        ActivityPrizeDO activityPrize = activityPrizeDO(
                1L, 10L, 3L, PrizeTierEnum.FIRST_PRIZE,
                ActivityPrizeStatusEnum.PENDING);
        when(activityPrizeMapper.selectOne(any())).thenReturn(activityPrize);
        when(activityPrizeMapper.selectList(any())).thenReturn(List.of(activityPrize));
        when(prizeMapper.selectById(10L)).thenReturn(prizeDO(10L, "手机"));

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1050, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenWinnerDuplicated() {
        DrawPrizeCommand command = validCommand();
        command.setWinnerIds(List.of(1L, 1L));

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(command));

        assertEquals(1051, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenWinnerNotPending() {
        when(activityMapper.selectByIdForUpdate(100L))
                .thenReturn(activityDO(100L, "年会抽奖",
                        ActivityStatusEnum.RUNNING.getCode()));
        ActivityPrizeDO activityPrize = activityPrizeDO(
                1L, 10L, 2L, PrizeTierEnum.FIRST_PRIZE,
                ActivityPrizeStatusEnum.PENDING);
        when(activityPrizeMapper.selectOne(any())).thenReturn(activityPrize);
        when(activityPrizeMapper.selectList(any())).thenReturn(List.of(activityPrize));
        when(prizeMapper.selectById(10L)).thenReturn(prizeDO(10L, "手机"));
        when(activityUserMapper.selectList(any())).thenReturn(List.of(
                activityUserDO(1L, 1L, "张三")));

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1052, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenWinnerContactMissing() {
        stubSuccessfulDraw();
        when(userMapper.selectBatchIds(any())).thenReturn(List.of(
                userDO(1L, "张三", "zhangsan@test.com", "13800000001"),
                userDO(2L, "李四", "", "13800000002")));

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1053, e.getCode());
        verify(winningRecordMapper, never()).insertBatch(any());
    }

    @Test
    void drawPrizeThrowsWhenCommandEmpty() {
        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommandWithNoWinner()));

        assertEquals(1046, e.getCode());
    }

    @Test
    void drawPrizeWrapsInsertFailure() {
        stubSuccessfulDraw();
        when(winningRecordMapper.insertBatch(any()))
                .thenThrow(new RuntimeException("db error"));

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1054, e.getCode());
    }

    @Test
    void drawPrizeIgnoresCacheAndNotifyFailure() {
        stubSuccessfulDraw();
        doThrow(new RuntimeException("redis down"))
                .when(activityCacheService).evictActivityDetail(100L);
        doThrow(new RuntimeException("mq down"))
                .when(drawMessageProducer).sendNotify(any());

        DrawPrizeDTO result = drawPrizeService.drawPrize(validCommand());

        assertEquals(2, result.getWinnerCount());
    }

    @Test
    void drawPrizeThrowsWhenRecordInsertCountMismatch() {
        stubSuccessfulDraw();
        when(winningRecordMapper.insertBatch(any())).thenReturn(1);

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1054, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenPrizeDrawnConcurrently() {
        stubSuccessfulDraw();
        when(activityPrizeMapper.update(any(), any())).thenReturn(0);

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1049, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenActivityUserCountMismatch() {
        stubSuccessfulDraw();
        when(activityUserMapper.update(any(), any())).thenReturn(1);

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1052, e.getCode());
    }

    @Test
    void drawPrizeThrowsWhenDrawRequestSuccessUpdateFails() {
        stubSuccessfulDraw();
        when(drawRequestMapper.update(any(), any())).thenReturn(0);

        ServiceException e = assertThrows(ServiceException.class,
                () -> drawPrizeService.drawPrize(validCommand()));

        assertEquals(1054, e.getCode());
    }

    @Test
    void drawPrizeKeepsActivityRunningWhenPendingPrizesRemain() {
        stubSuccessfulDraw();
        when(activityPrizeMapper.selectCount(any())).thenReturn(1L);

        DrawPrizeDTO result = drawPrizeService.drawPrize(validCommand());

        assertFalse(result.isActivityFinished());
        verify(activityMapper, never()).update(any(), any());
        // 活动未结束时不收尾未中奖人员，只更新本轮中奖人员
        verify(activityUserMapper, times(1)).update(any(), any());
    }

    @Test
    void drawPrizeSkipsDrawRequestUpdateWhenRequestIdIsNull() {
        stubSuccessfulDraw();
        DrawPrizeCommand command = validCommand();
        command.setRequestId(null);

        DrawPrizeDTO result = drawPrizeService.drawPrize(command);

        assertEquals(2, result.getWinnerCount());
        verify(drawRequestMapper, never()).update(any(), any());
    }

    private DrawPrizeCommand validCommand() {
        DrawPrizeCommand command = new DrawPrizeCommand();
        command.setRequestId(1L);
        command.setActivityId(100L);
        command.setPrizeId(10L);
        command.setWinnerIds(List.of(1L, 2L));
        return command;
    }

    private DrawPrizeCommand validCommandWithNoWinner() {
        DrawPrizeCommand command = validCommand();
        command.setWinnerIds(List.of());
        return command;
    }

    private void stubSuccessfulDraw() {
        when(activityMapper.selectByIdForUpdate(100L))
                .thenReturn(activityDO(100L, "年会抽奖",
                        ActivityStatusEnum.RUNNING.getCode()));
        ActivityPrizeDO activityPrize = activityPrizeDO(
                1L, 10L, 2L, PrizeTierEnum.FIRST_PRIZE,
                ActivityPrizeStatusEnum.PENDING);
        when(activityPrizeMapper.selectOne(any())).thenReturn(activityPrize);
        when(activityPrizeMapper.selectList(any())).thenReturn(List.of(activityPrize));
        when(prizeMapper.selectById(10L)).thenReturn(prizeDO(10L, "手机"));
        when(activityUserMapper.selectList(any())).thenReturn(List.of(
                activityUserDO(1L, 1L, "张三"),
                activityUserDO(2L, 2L, "李四")));
        when(userMapper.selectBatchIds(any())).thenReturn(List.of(
                userDO(1L, "张三", "zhangsan@test.com", "13800000001"),
                userDO(2L, "李四", "lisi@test.com", "13800000002")));
        when(winningRecordMapper.insertBatch(any())).thenReturn(2);
        when(activityPrizeMapper.update(any(), any())).thenReturn(1);
        when(activityUserMapper.update(any(), any())).thenReturn(2);
        when(activityPrizeMapper.selectCount(any())).thenReturn(0L);
        when(activityMapper.update(any(), any())).thenReturn(1);
        when(drawRequestMapper.update(any(), any())).thenReturn(1);
    }

    private ActivityDO activityDO(Long id, String name, String status) {
        ActivityDO activityDO = new ActivityDO();
        activityDO.setId(id);
        activityDO.setActivityName(name);
        activityDO.setDescription(name + "描述");
        activityDO.setStatus(status);
        return activityDO;
    }

    private ActivityPrizeDO activityPrizeDO(Long id, Long prizeId, Long amount,
                                            PrizeTierEnum tier,
                                            ActivityPrizeStatusEnum status) {
        ActivityPrizeDO activityPrizeDO = new ActivityPrizeDO();
        activityPrizeDO.setId(id);
        activityPrizeDO.setActivityId(100L);
        activityPrizeDO.setPrizeId(prizeId);
        activityPrizeDO.setPrizeAmount(amount);
        activityPrizeDO.setPrizeTiers(tier.getCode());
        activityPrizeDO.setStatus(status.getCode());
        return activityPrizeDO;
    }

    private ActivityUserDO activityUserDO(Long id, Long userId, String userName) {
        ActivityUserDO activityUserDO = new ActivityUserDO();
        activityUserDO.setId(id);
        activityUserDO.setActivityId(100L);
        activityUserDO.setUserId(userId);
        activityUserDO.setUserName(userName);
        activityUserDO.setStatus(ActivityUserStatusEnum.PENDING.getCode());
        return activityUserDO;
    }

    private UserDO userDO(Long id, String userName, String email, String phoneNumber) {
        UserDO userDO = new UserDO();
        userDO.setId(id);
        userDO.setUserName(userName);
        userDO.setEmail(email);
        userDO.setPhoneNumber(new Encrypt(phoneNumber));
        return userDO;
    }

    private PrizeDO prizeDO(Long id, String name) {
        PrizeDO prizeDO = new PrizeDO();
        prizeDO.setId(id);
        prizeDO.setName(name);
        prizeDO.setDescription(name + "描述");
        prizeDO.setPrice(new BigDecimal("6999.00"));
        return prizeDO;
    }
}
