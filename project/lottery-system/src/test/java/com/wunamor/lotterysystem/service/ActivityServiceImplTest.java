package com.wunamor.lotterysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wunamor.lotterysystem.common.enums.ActivityStatusEnum;
import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.mapper.ActivityMapper;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.ActivityUserMapper;
import com.wunamor.lotterysystem.mapper.PrizeMapper;
import com.wunamor.lotterysystem.mapper.UserMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.mapper.dataobject.PrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import com.wunamor.lotterysystem.service.ActivityCacheService;
import com.wunamor.lotterysystem.service.command.ActivityCreateCommand;
import com.wunamor.lotterysystem.service.command.ActivityListCommand;
import com.wunamor.lotterysystem.service.command.ActivityPrizeCommand;
import com.wunamor.lotterysystem.service.command.ActivityUserCommand;
import com.wunamor.lotterysystem.service.dto.ActivityCreateDTO;
import com.wunamor.lotterysystem.service.dto.ActivityDetailDTO;
import com.wunamor.lotterysystem.service.dto.ActivityListDTO;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.wunamor.lotterysystem.service.enums.ActivityUserStatusEnum;
import com.wunamor.lotterysystem.service.impl.ActivityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActivityServiceImplTest {

    private ActivityMapper activityMapper;
    private ActivityPrizeMapper activityPrizeMapper;
    private ActivityUserMapper activityUserMapper;
    private PrizeMapper prizeMapper;
    private UserMapper userMapper;
    private ActivityCacheService activityCacheService;
    private ActivityServiceImpl activityService;

    @BeforeEach
    void setUp() {
        activityMapper = mock(ActivityMapper.class);
        activityPrizeMapper = mock(ActivityPrizeMapper.class);
        activityUserMapper = mock(ActivityUserMapper.class);
        prizeMapper = mock(PrizeMapper.class);
        userMapper = mock(UserMapper.class);
        activityCacheService = mock(ActivityCacheService.class);
        activityService = new ActivityServiceImpl(activityMapper, activityPrizeMapper,
                activityUserMapper, prizeMapper, userMapper, activityCacheService);
    }

    @Test
    void createActivityInsertsActivityPrizeAndUser() {
        ActivityCreateCommand command = validCommand();
        stubPrizesAndUsers();
        stubActivityInsert();

        ActivityCreateDTO dto = activityService.createActivity(command);

        assertEquals(100L, dto.getActivityId());
        assertActivityInserted();
        assertActivityPrizeInserted();
        assertActivityUsersInserted();
    }

    @Test
    void createActivityCachesDetailSnapshot() {
        stubPrizesAndUsers();
        stubActivityInsert();

        activityService.createActivity(validCommand());

        ArgumentCaptor<ActivityDetailDTO> captor = ArgumentCaptor.forClass(ActivityDetailDTO.class);
        verify(activityCacheService).cacheActivityDetail(captor.capture());
        ActivityDetailDTO detail = captor.getValue();
        assertEquals(100L, detail.getActivityId());
        assertEquals("年会抽奖", detail.getActivityName());
        assertEquals("2026 年会", detail.getDescription());
        assertTrue(detail.isValid());
        assertEquals(1, detail.getPrizes().size());
        assertEquals(10L, detail.getPrizes().get(0).getPrizeId());
        assertEquals("奖品10", detail.getPrizes().get(0).getName());
        assertEquals(2L, detail.getPrizes().get(0).getPrizeAmount());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getCode(), detail.getPrizes().get(0).getPrizeTier());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getDesc(),
                detail.getPrizes().get(0).getPrizeTierName());
        assertTrue(detail.getPrizes().get(0).isValid());
        assertTrue(detail.getPrizes().get(0).getList().isEmpty());
        assertEquals(2, detail.getUsers().size());
        assertEquals(1L, detail.getUsers().get(0).getUserId());
        assertEquals("张三", detail.getUsers().get(0).getUserName());
    }

    @Test
    void createActivityCachesOnlyAfterCommitWhenTransactionActive() {
        stubPrizesAndUsers();
        stubActivityInsert();
        TransactionSynchronizationManager.initSynchronization();
        try {
            activityService.createActivity(validCommand());

            verify(activityCacheService, never()).cacheActivityDetail(any());

            TransactionSynchronizationManager.getSynchronizations()
                    .forEach(TransactionSynchronization::afterCommit);
            verify(activityCacheService).cacheActivityDetail(any());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void nullCommandIsRejected() {
        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(null));
        assertEquals(1033, e.getCode());
    }

    @Test
    void emptyNameIsRejected() {
        ActivityCreateCommand command = validCommand();
        command.setActivityName("  ");

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1034, e.getCode());
    }

    @Test
    void tooLongNameIsRejected() {
        ActivityCreateCommand command = validCommand();
        command.setActivityName("x".repeat(256));

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1035, e.getCode());
    }

    @Test
    void emptyDescriptionIsRejected() {
        ActivityCreateCommand command = validCommand();
        command.setDescription("  ");

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1036, e.getCode());
    }

    @Test
    void tooLongDescriptionIsRejected() {
        ActivityCreateCommand command = validCommand();
        command.setDescription("x".repeat(256));

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1037, e.getCode());
    }

    @Test
    void emptyPrizeListIsRejected() {
        ActivityCreateCommand command = validCommand();
        command.setActivityPrizeList(new ArrayList<>());

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1038, e.getCode());
    }

    @Test
    void emptyUserListIsRejected() {
        ActivityCreateCommand command = validCommand();
        command.setActivityUserList(new ArrayList<>());

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1040, e.getCode());
    }

    @Test
    void duplicatePrizeIsRejected() {
        ActivityCreateCommand command = validCommand();
        ActivityPrizeCommand duplicate = new ActivityPrizeCommand();
        duplicate.setPrizeId(10L);
        duplicate.setPrizeAmount(1L);
        duplicate.setPrizeTier(PrizeTierEnum.SECOND_PRIZE);
        command.getActivityPrizeList().add(duplicate);

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1039, e.getCode());
    }

    @Test
    void duplicateUserIsRejected() {
        ActivityCreateCommand command = validCommand();
        ActivityUserCommand duplicate = new ActivityUserCommand();
        duplicate.setUserId(1L);
        command.getActivityUserList().add(duplicate);

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1041, e.getCode());
    }

    @Test
    void missingPrizeIsRejected() {
        when(prizeMapper.selectBatchIds(any())).thenReturn(List.of());

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(validCommand()));
        assertEquals(1039, e.getCode());
    }

    @Test
    void missingUserIsRejected() {
        when(prizeMapper.selectBatchIds(any())).thenReturn(List.of(prizeDO(10L)));
        when(userMapper.listUsersByIds(any())).thenReturn(List.of(userDO(1L, "张三")));

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(validCommand()));
        assertEquals(1041, e.getCode());
    }

    @Test
    void notEnoughUsersIsRejected() {
        ActivityCreateCommand command = validCommand();
        command.getActivityPrizeList().get(0).setPrizeAmount(3L);
        stubPrizesAndUsers();

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(command));
        assertEquals(1042, e.getCode());
    }

    @Test
    void insertFailureIsWrappedAsCreateError() {
        stubPrizesAndUsers();
        when(activityMapper.insert(any(ActivityDO.class)))
                .thenThrow(new RuntimeException("db error"));

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(validCommand()));
        assertEquals(1043, e.getCode());
    }

    @Test
    void batchInsertFailureIsWrappedAsCreateError() {
        stubPrizesAndUsers();
        stubActivityInsert();
        when(activityPrizeMapper.insertBatch(any()))
                .thenThrow(new RuntimeException("db error"));

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.createActivity(validCommand()));
        assertEquals(1043, e.getCode());
    }

    @Test
    void listActivitiesReturnsPage() {
        ActivityListCommand command = new ActivityListCommand();
        command.setCurrentPage(1);
        command.setPageSize(5);

        Page<ActivityDO> dbPage = new Page<>(1, 5);
        dbPage.setTotal(12);
        dbPage.setRecords(List.of(
                activityDO(3L, "活动三", ActivityStatusEnum.RUNNING.getCode()),
                activityDO(2L, "活动二", ActivityStatusEnum.FINISHED.getCode())));
        doReturn(dbPage).when(activityMapper).selectPage(any(Page.class), any());

        PageResult<ActivityListDTO> result = activityService.listActivities(command);

        assertEquals(12, result.getTotal());
        assertEquals(1, result.getCurrentPage());
        assertEquals(5, result.getPageSize());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getRecords().size());
        assertTrue(result.getRecords().get(0).isValid());
        assertFalse(result.getRecords().get(1).isValid());
    }

    @Test
    @SuppressWarnings("unchecked")
    void listActivitiesNormalizesInvalidPage() {
        ActivityListCommand command = new ActivityListCommand();
        command.setCurrentPage(0);
        command.setPageSize(1000);

        Page<ActivityDO> dbPage = new Page<>(1, 100);
        dbPage.setTotal(0);
        dbPage.setRecords(List.of());
        doReturn(dbPage).when(activityMapper).selectPage(any(Page.class), any());

        activityService.listActivities(command);

        ArgumentCaptor<Page<ActivityDO>> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(activityMapper).selectPage(pageCaptor.capture(), any());
        assertEquals(1L, pageCaptor.getValue().getCurrent());
        assertEquals(100L, pageCaptor.getValue().getSize());
    }

    @Test
    void listActivitiesEmpty() {
        ActivityListCommand command = new ActivityListCommand();

        Page<ActivityDO> dbPage = new Page<>(1, 10);
        dbPage.setTotal(0);
        dbPage.setRecords(List.of());
        doReturn(dbPage).when(activityMapper).selectPage(any(Page.class), any());

        PageResult<ActivityListDTO> result = activityService.listActivities(command);

        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    void getActivityDetailReturnsCacheWhenHit() {
        ActivityDetailDTO cached = new ActivityDetailDTO();
        cached.setActivityId(100L);
        when(activityCacheService.getActivityDetail(100L)).thenReturn(cached);

        ActivityDetailDTO result = activityService.getActivityDetail(100L);

        assertEquals(100L, result.getActivityId());
        verify(activityMapper, never()).selectById(any());
        verify(activityCacheService, never()).cacheActivityDetail(any());
    }

    @Test
    void getActivityDetailRebuildsFromDatabaseWhenCacheMiss() {
        when(activityCacheService.getActivityDetail(100L)).thenReturn(null);
        when(activityMapper.selectById(100L))
                .thenReturn(activityDO(100L, "年会抽奖", ActivityStatusEnum.RUNNING.getCode()));
        when(activityPrizeMapper.selectList(any())).thenReturn(List.of(
                activityPrizeDO(1L, 10L, 2L, PrizeTierEnum.FIRST_PRIZE,
                        ActivityPrizeStatusEnum.PENDING),
                activityPrizeDO(2L, 11L, 1L, PrizeTierEnum.SECOND_PRIZE,
                        ActivityPrizeStatusEnum.DRAWN)));
        when(prizeMapper.selectBatchIds(any())).thenReturn(List.of(
                prizeDO(10L), prizeDO(11L)));
        when(activityUserMapper.selectList(any())).thenReturn(List.of(
                activityUserDO(1L, 1L, "张三"),
                activityUserDO(2L, 2L, "李四")));

        ActivityDetailDTO detail = activityService.getActivityDetail(100L);

        assertTrue(detail.isValid());
        assertEquals("年会抽奖", detail.getActivityName());
        assertEquals(2, detail.getPrizes().size());
        assertTrue(detail.getPrizes().get(0).isValid());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getDesc(),
                detail.getPrizes().get(0).getPrizeTierName());
        assertFalse(detail.getPrizes().get(1).isValid());
        assertTrue(detail.getPrizes().get(1).getList().isEmpty());
        assertEquals(2, detail.getUsers().size());
        assertEquals("张三", detail.getUsers().get(0).getUserName());
        verify(activityCacheService).cacheActivityDetail(detail);
    }

    @Test
    void getActivityDetailThrowsWhenActivityNotFound() {
        when(activityCacheService.getActivityDetail(100L)).thenReturn(null);
        when(activityMapper.selectById(100L)).thenReturn(null);

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.getActivityDetail(100L));

        assertEquals(1045, e.getCode());
        verify(activityCacheService, never()).cacheActivityDetail(any());
    }

    @Test
    void getActivityDetailThrowsWhenPrizeMissing() {
        when(activityCacheService.getActivityDetail(100L)).thenReturn(null);
        when(activityMapper.selectById(100L))
                .thenReturn(activityDO(100L, "年会抽奖", ActivityStatusEnum.RUNNING.getCode()));
        when(activityPrizeMapper.selectList(any())).thenReturn(List.of(
                activityPrizeDO(1L, 10L, 2L, PrizeTierEnum.FIRST_PRIZE,
                        ActivityPrizeStatusEnum.PENDING)));
        when(prizeMapper.selectBatchIds(any())).thenReturn(List.of());

        ServiceException e = assertThrows(ServiceException.class,
                () -> activityService.getActivityDetail(100L));

        assertEquals(1039, e.getCode());
        verify(activityCacheService, never()).cacheActivityDetail(any());
    }

    @Test
    void getActivityDetailRejectsInvalidId() {
        ServiceException nullId = assertThrows(ServiceException.class,
                () -> activityService.getActivityDetail(null));
        assertEquals(1044, nullId.getCode());

        ServiceException zeroId = assertThrows(ServiceException.class,
                () -> activityService.getActivityDetail(0L));
        assertEquals(1044, zeroId.getCode());
    }

    private ActivityCreateCommand validCommand() {
        ActivityPrizeCommand prize = new ActivityPrizeCommand();
        prize.setPrizeId(10L);
        prize.setPrizeAmount(2L);
        prize.setPrizeTier(PrizeTierEnum.FIRST_PRIZE);

        ActivityUserCommand user1 = new ActivityUserCommand();
        user1.setUserId(1L);
        ActivityUserCommand user2 = new ActivityUserCommand();
        user2.setUserId(2L);

        ActivityCreateCommand command = new ActivityCreateCommand();
        command.setActivityName("年会抽奖");
        command.setDescription("2026 年会");
        command.setActivityPrizeList(new ArrayList<>(List.of(prize)));
        command.setActivityUserList(new ArrayList<>(List.of(user1, user2)));
        return command;
    }

    private void stubPrizesAndUsers() {
        when(prizeMapper.selectBatchIds(any())).thenReturn(List.of(prizeDO(10L)));
        when(userMapper.listUsersByIds(any())).thenReturn(List.of(
                userDO(1L, "张三"), userDO(2L, "李四")));
    }

    private void stubActivityInsert() {
        when(activityMapper.insert(any(ActivityDO.class))).thenAnswer(invocation -> {
            ActivityDO activityDO = invocation.getArgument(0);
            activityDO.setId(100L);
            return 1;
        });
    }

    private void assertActivityInserted() {
        ArgumentCaptor<ActivityDO> captor = ArgumentCaptor.forClass(ActivityDO.class);
        verify(activityMapper).insert(captor.capture());
        assertEquals("年会抽奖", captor.getValue().getActivityName());
        assertEquals("2026 年会", captor.getValue().getDescription());
        assertEquals(ActivityStatusEnum.RUNNING.getCode(), captor.getValue().getStatus());
    }

    @SuppressWarnings("unchecked")
    private void assertActivityPrizeInserted() {
        ArgumentCaptor<List<ActivityPrizeDO>> captor = ArgumentCaptor.forClass(List.class);
        verify(activityPrizeMapper).insertBatch(captor.capture());
        List<ActivityPrizeDO> rows = captor.getValue();
        assertEquals(1, rows.size());
        ActivityPrizeDO row = rows.get(0);
        assertEquals(100L, row.getActivityId());
        assertEquals(10L, row.getPrizeId());
        assertEquals(2L, row.getPrizeAmount());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getCode(), row.getPrizeTiers());
        assertEquals(ActivityPrizeStatusEnum.PENDING.getCode(), row.getStatus());
    }

    @SuppressWarnings("unchecked")
    private void assertActivityUsersInserted() {
        ArgumentCaptor<List<ActivityUserDO>> captor = ArgumentCaptor.forClass(List.class);
        verify(activityUserMapper).insertBatch(captor.capture());
        List<ActivityUserDO> users = captor.getValue();
        assertEquals(2, users.size());
        assertEquals("张三", users.get(0).getUserName());
        assertEquals("李四", users.get(1).getUserName());
        assertEquals(ActivityUserStatusEnum.PENDING.getCode(), users.get(0).getStatus());
    }

    private PrizeDO prizeDO(Long id) {
        PrizeDO prizeDO = new PrizeDO();
        prizeDO.setId(id);
        prizeDO.setName("奖品" + id);
        return prizeDO;
    }

    private UserDO userDO(Long id, String userName) {
        UserDO userDO = new UserDO();
        userDO.setId(id);
        userDO.setUserName(userName);
        return userDO;
    }

    private ActivityDO activityDO(Long id, String name, String status) {
        ActivityDO activityDO = new ActivityDO();
        activityDO.setId(id);
        activityDO.setActivityName(name);
        activityDO.setDescription(name + "描述");
        activityDO.setStatus(status);
        return activityDO;
    }

    private ActivityPrizeDO activityPrizeDO(Long id, Long prizeId, Long prizeAmount,
                                            PrizeTierEnum prizeTier,
                                            ActivityPrizeStatusEnum status) {
        ActivityPrizeDO activityPrizeDO = new ActivityPrizeDO();
        activityPrizeDO.setId(id);
        activityPrizeDO.setActivityId(100L);
        activityPrizeDO.setPrizeId(prizeId);
        activityPrizeDO.setPrizeAmount(prizeAmount);
        activityPrizeDO.setPrizeTiers(prizeTier.getCode());
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
}
