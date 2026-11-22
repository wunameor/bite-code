package com.wunamor.lotterysystem.service.draw.state.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityUserMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.draw.state.DrawStatePhase;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdater;
import com.wunamor.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.springframework.stereotype.Component;

/**
 * 活动人员未中奖状态更新步骤。
 *
 * <p>活动所有奖品抽取完成后，把仍处于待中奖状态的活动人员统一更新为未中奖；
 * 中途抽奖时未抽中的人员继续保留 PENDING，仍可参与后续奖品。</p>
 */
@Component
public class ActivityUserNotWonUpdater implements DrawStateUpdater {

    /** 活动人员 Mapper，用于批量收尾未中奖人员。 */
    private final ActivityUserMapper activityUserMapper;

    /**
     * 构造活动人员未中奖状态更新步骤。
     *
     * @param activityUserMapper 活动人员 Mapper
     */
    public ActivityUserNotWonUpdater(ActivityUserMapper activityUserMapper) {
        this.activityUserMapper = activityUserMapper;
    }

    /**
     * 返回活动阶段，保证在活动结束判断之后执行。
     *
     * @return 活动状态阶段
     */
    @Override
    public DrawStatePhase phase() {
        return DrawStatePhase.ACTIVITY;
    }

    /**
     * 同阶段内排在 ActivityFinishedUpdater 之后，此时活动是否结束已经写入上下文。
     *
     * @return 阶段内优先级
     */
    @Override
    public int order() {
        return 10;
    }

    /**
     * 只有本次抽奖让活动结束时，才需要收尾未中奖人员。
     *
     * @param context 抽奖上下文
     * @return true 表示活动已结束
     */
    @Override
    public boolean shouldExecute(DrawContext context) {
        return context.isActivityFinished();
    }

    /**
     * 把本活动内仍处于待中奖状态的人员批量更新为未中奖。
     *
     * @param context 抽奖上下文
     */
    @Override
    public void execute(DrawContext context) {
        // 1. 活动 id 是批量更新的范围条件，缺失时不能继续
        Long activityId = context.getActivityId();
        if (activityId == null) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_NOT_FOUND);
        }

        // 2. 只更新仍为 PENDING 的人员，已中奖的 WINNER 不会被命中
        activityUserMapper.update(null,
                new LambdaUpdateWrapper<ActivityUserDO>()
                        .eq(ActivityUserDO::getActivityId, activityId)
                        .eq(ActivityUserDO::getStatus,
                                ActivityUserStatusEnum.PENDING.getCode())
                        .set(ActivityUserDO::getStatus,
                                ActivityUserStatusEnum.NOT_WON.getCode()));
    }
}
