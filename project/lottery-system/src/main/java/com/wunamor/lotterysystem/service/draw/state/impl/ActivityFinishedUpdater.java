package com.wunamor.lotterysystem.service.draw.state.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wunamor.lotterysystem.common.enums.ActivityStatusEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityMapper;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.draw.state.DrawStatePhase;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdater;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import org.springframework.stereotype.Component;

/**
 * 活动结束状态更新步骤。
 *
 * <p>所有活动奖品都抽取完成后，把活动从 RUNNING 更新为 FINISHED；
 * 仍有待抽奖品时只记录活动未结束，不修改活动状态。</p>
 */
@Component
public class ActivityFinishedUpdater implements DrawStateUpdater {

    /** 活动 Mapper，用于读取和更新活动状态。 */
    private final ActivityMapper activityMapper;

    /** 活动奖品 Mapper，用于统计是否还有待抽取奖品。 */
    private final ActivityPrizeMapper activityPrizeMapper;

    /**
     * 构造活动结束状态更新步骤。
     *
     * @param activityMapper 活动 Mapper
     * @param activityPrizeMapper 活动奖品 Mapper
     */
    public ActivityFinishedUpdater(ActivityMapper activityMapper,
                                   ActivityPrizeMapper activityPrizeMapper) {
        this.activityMapper = activityMapper;
        this.activityPrizeMapper = activityPrizeMapper;
    }

    /**
     * 返回活动结束阶段，保证在奖品和人员状态更新之后执行。
     *
     * @return 活动状态阶段
     */
    @Override
    public DrawStatePhase phase() {
        return DrawStatePhase.ACTIVITY;
    }

    /**
     * 根据剩余待抽取奖品数量决定是否结束活动。
     *
     * @param context 抽奖上下文
     */
    @Override
    public void execute(DrawContext context) {
        // 1. 活动是状态更新的必要上下文
        ActivityDO activity = context.getActivity();
        if (activity == null) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_NOT_FOUND);
        }

        // 2. 仍有待抽取奖品时，活动继续保持进行中
        Long pendingCount = activityPrizeMapper.selectCount(
                new LambdaQueryWrapper<ActivityPrizeDO>()
                        .eq(ActivityPrizeDO::getActivityId, activity.getId())
                        .eq(ActivityPrizeDO::getStatus,
                                ActivityPrizeStatusEnum.PENDING.getCode()));
        if (pendingCount != null && pendingCount > 0) {
            context.setActivityFinished(false);
            return;
        }

        // 3. 所有奖品已抽完，只把进行中的活动更新为已完成
        activityMapper.update(null, new LambdaUpdateWrapper<ActivityDO>()
                .eq(ActivityDO::getId, activity.getId())
                .eq(ActivityDO::getStatus, ActivityStatusEnum.RUNNING.getCode())
                .set(ActivityDO::getStatus, ActivityStatusEnum.FINISHED.getCode()));
        context.setActivityFinished(true);
    }
}
