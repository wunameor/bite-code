package com.wunamor.lotterysystem.service.draw.state.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.draw.state.DrawStatePhase;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdater;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import org.springframework.stereotype.Component;

/**
 * 活动奖品抽取状态更新步骤。
 *
 * <p>把当前奖品从 PENDING 更新为 DRAWN，更新失败通常表示奖品已被其他请求抽走。</p>
 */
@Component
public class PrizeDrawnUpdater implements DrawStateUpdater {

    /** 活动奖品 Mapper，用于更新活动奖品的抽取状态。 */
    private final ActivityPrizeMapper activityPrizeMapper;

    /**
     * 构造活动奖品状态更新步骤。
     *
     * @param activityPrizeMapper 活动奖品 Mapper
     */
    public PrizeDrawnUpdater(ActivityPrizeMapper activityPrizeMapper) {
        this.activityPrizeMapper = activityPrizeMapper;
    }

    /**
     * 返回奖品阶段，保证在中奖记录写入之后执行。
     *
     * @return 活动奖品状态阶段
     */
    @Override
    public DrawStatePhase phase() {
        return DrawStatePhase.PRIZE;
    }

    /**
     * 把待抽取的活动奖品更新为已抽取。
     *
     * @param context 抽奖上下文
     */
    @Override
    public void execute(DrawContext context) {
        // 1. 活动奖品是状态更新的必要上下文
        ActivityPrizeDO activityPrize = context.getActivityPrize();
        if (activityPrize == null) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_INVALID);
        }

        // 2. 只更新仍处于待抽取状态的活动奖品
        int updated = activityPrizeMapper.update(null,
                new LambdaUpdateWrapper<ActivityPrizeDO>()
                        .eq(ActivityPrizeDO::getId, activityPrize.getId())
                        .eq(ActivityPrizeDO::getStatus,
                                ActivityPrizeStatusEnum.PENDING.getCode())
                        .set(ActivityPrizeDO::getStatus,
                                ActivityPrizeStatusEnum.DRAWN.getCode()));
        // 3. 影响行数必须为 1，否则说明奖品状态已变化
        if (updated != 1) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ALREADY_DRAWN);
        }
    }
}
