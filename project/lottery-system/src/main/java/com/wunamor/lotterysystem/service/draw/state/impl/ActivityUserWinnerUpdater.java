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

import java.util.List;

/**
 * 活动人员中奖状态更新步骤。
 *
 * <p>把本次抽中的活动人员从 PENDING 更新为 WINNER，并校验所有中奖人都完成状态翻转。</p>
 */
@Component
public class ActivityUserWinnerUpdater implements DrawStateUpdater {

    /** 活动人员 Mapper，用于更新活动内人员状态。 */
    private final ActivityUserMapper activityUserMapper;

    /**
     * 构造活动人员中奖状态更新步骤。
     *
     * @param activityUserMapper 活动人员 Mapper
     */
    public ActivityUserWinnerUpdater(ActivityUserMapper activityUserMapper) {
        this.activityUserMapper = activityUserMapper;
    }

    /**
     * 返回活动人员阶段，保证在中奖记录和奖品状态之后执行。
     *
     * @return 活动人员状态阶段
     */
    @Override
    public DrawStatePhase phase() {
        return DrawStatePhase.ACTIVITY_USER;
    }

    /**
     * 把本次中奖人员从待中奖状态更新为已中奖状态。
     *
     * @param context 抽奖上下文
     */
    @Override
    public void execute(DrawContext context) {
        // 1. 中奖人员列表是更新活动人员状态的必要条件
        List<Long> winnerIds = context.getWinnerIds();
        if (winnerIds == null || winnerIds.isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_INFO_IS_EMPTY);
        }

        // 2. 只更新本次活动中仍处于待中奖状态的人员
        int updated = activityUserMapper.update(null,
                new LambdaUpdateWrapper<ActivityUserDO>()
                        .eq(ActivityUserDO::getActivityId, context.getActivityId())
                        .in(ActivityUserDO::getUserId, winnerIds)
                        .eq(ActivityUserDO::getStatus,
                                ActivityUserStatusEnum.PENDING.getCode())
                        .set(ActivityUserDO::getStatus,
                                ActivityUserStatusEnum.WINNER.getCode()));
        // 3. 更新行数必须与中奖人数一致，避免部分人员状态未写入
        if (updated != winnerIds.size()) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_WINNER_INVALID);
        }
    }
}
