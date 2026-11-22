package com.wunamor.lotterysystem.service.draw.state.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityUserMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 活动人员未中奖状态更新步骤测试。
 */
class ActivityUserNotWonUpdaterTest {

    private ActivityUserMapper activityUserMapper;
    private ActivityUserNotWonUpdater updater;

    /**
     * 初始化 ActivityUserDO 的表信息，保证 LambdaUpdateWrapper 能解析列名。
     */
    @BeforeAll
    static void initTableInfo() {
        MapperBuilderAssistant assistant =
                new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, ActivityUserDO.class);
    }

    @BeforeEach
    void setUp() {
        activityUserMapper = mock(ActivityUserMapper.class);
        updater = new ActivityUserNotWonUpdater(activityUserMapper);
    }

    /**
     * 活动未结束时不应该收尾未中奖人员。
     */
    @Test
    void shouldNotExecuteWhenActivityNotFinished() {
        DrawContext context = new DrawContext();
        context.setActivityFinished(false);

        assertFalse(updater.shouldExecute(context));
    }

    /**
     * 活动结束时才执行未中奖收尾。
     */
    @Test
    void shouldExecuteWhenActivityFinished() {
        DrawContext context = new DrawContext();
        context.setActivityFinished(true);

        assertTrue(updater.shouldExecute(context));
    }

    /**
     * 更新条件必须限定活动 id 和 PENDING 状态，目标状态为 NOT_WON。
     */
    @Test
    @SuppressWarnings("unchecked")
    void executeUpdatesPendingUsersToNotWon() {
        DrawContext context = new DrawContext();
        context.setActivityId(100L);
        context.setActivityFinished(true);

        updater.execute(context);

        ArgumentCaptor<LambdaUpdateWrapper<ActivityUserDO>> captor =
                ArgumentCaptor.forClass(LambdaUpdateWrapper.class);
        verify(activityUserMapper).update(isNull(), captor.capture());

        LambdaUpdateWrapper<ActivityUserDO> wrapper = captor.getValue();
        assertTrue(wrapper.getSqlSegment().contains("activity_id"));
        assertTrue(wrapper.getParamNameValuePairs().containsValue(100L));
        assertTrue(wrapper.getParamNameValuePairs()
                .containsValue(ActivityUserStatusEnum.PENDING.getCode()));
        assertTrue(wrapper.getParamNameValuePairs()
                .containsValue(ActivityUserStatusEnum.NOT_WON.getCode()));
    }

    /**
     * 活动 id 缺失时不能执行批量更新。
     */
    @Test
    void executeRejectsMissingActivityId() {
        DrawContext context = new DrawContext();
        context.setActivityFinished(true);

        assertThrows(ServiceException.class, () -> updater.execute(context));
    }
}
