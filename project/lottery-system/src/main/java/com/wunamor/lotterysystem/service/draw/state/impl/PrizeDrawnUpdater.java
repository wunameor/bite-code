package com.wunamor.lotterysystem.service.draw.state.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.ActivityPrizeMapper;
import com.wunamor.lotterysystem.mapper.WinningRecordMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.draw.state.DrawStatePhase;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdater;
import com.wunamor.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import org.springframework.stereotype.Component;

/**
 * 活动奖品抽取状态更新步骤。
 *
 * <p>抽奖支持按人中奖：奖品在中奖人数抽满之前保持 PENDING，可以继续抽下一个人；
 * 只有已中奖人数达到奖品数量时，才把奖品从 PENDING 更新为 DRAWN。</p>
 */
@Component
public class PrizeDrawnUpdater implements DrawStateUpdater {

    /** 活动奖品 Mapper，用于更新活动奖品的抽取状态。 */
    private final ActivityPrizeMapper activityPrizeMapper;

    /** 中奖记录 Mapper，用于统计该奖品已经抽满多少人。 */
    private final WinningRecordMapper winningRecordMapper;

    /**
     * 构造活动奖品状态更新步骤。
     *
     * @param activityPrizeMapper  活动奖品 Mapper
     * @param winningRecordMapper  中奖记录 Mapper
     */
    public PrizeDrawnUpdater(ActivityPrizeMapper activityPrizeMapper,
                             WinningRecordMapper winningRecordMapper) {
        this.activityPrizeMapper = activityPrizeMapper;
        this.winningRecordMapper = winningRecordMapper;
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
     * 按该奖品已中奖人数决定是否把活动奖品更新为已抽取。
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

        // 2. 中奖记录在本阶段之前已经写入，这里统计的是包含本次中奖人在内的总人数
        long prizeAmount = activityPrize.getPrizeAmount() == null
                ? 0L : activityPrize.getPrizeAmount();
        long drawnCount = winningRecordMapper.countByActivityIdAndPrizeId(
                activityPrize.getActivityId(), activityPrize.getPrizeId());

        // 3. 还没抽满时奖品继续保持 PENDING，前端可以继续抽下一个人
        if (drawnCount < prizeAmount) {
            context.setPrizeFinished(false);
            return;
        }

        // 4. 抽满后只更新仍处于待抽取状态的活动奖品，避免覆盖并发状态
        int updated = activityPrizeMapper.update(null,
                new LambdaUpdateWrapper<ActivityPrizeDO>()
                        .eq(ActivityPrizeDO::getId, activityPrize.getId())
                        .eq(ActivityPrizeDO::getStatus,
                                ActivityPrizeStatusEnum.PENDING.getCode())
                        .set(ActivityPrizeDO::getStatus,
                                ActivityPrizeStatusEnum.DRAWN.getCode()));
        // 5. 影响行数必须为 1，否则说明奖品状态已被其他请求修改
        if (updated != 1) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ALREADY_DRAWN);
        }
        context.setPrizeFinished(true);
    }
}
