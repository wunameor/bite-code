package com.wunamor.lotterysystem.service.draw.state.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.mapper.DrawRequestMapper;
import com.wunamor.lotterysystem.mapper.dataobject.DrawRequestDO;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.draw.state.DrawStatePhase;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdater;
import com.wunamor.lotterysystem.service.dto.DrawWinnerDTO;
import com.wunamor.lotterysystem.service.enums.DrawRequestStatusEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽奖任务成功状态更新步骤。
 *
 * <p>在当前抽奖事务内把异步任务从 PROCESSING 更新为 SUCCESS，
 * 并保存中奖人员快照供前端轮询使用。</p>
 */
@Component
public class DrawRequestSuccessUpdater implements DrawStateUpdater {

    /** 抽奖任务 Mapper，用于更新任务终态和结果快照。 */
    private final DrawRequestMapper drawRequestMapper;

    /**
     * 构造抽奖任务成功状态更新步骤。
     *
     * @param drawRequestMapper 抽奖任务 Mapper
     */
    public DrawRequestSuccessUpdater(DrawRequestMapper drawRequestMapper) {
        this.drawRequestMapper = drawRequestMapper;
    }

    /**
     * 返回抽奖任务阶段，保证在业务状态全部更新完成后执行。
     *
     * @return 抽奖任务状态阶段
     */
    @Override
    public DrawStatePhase phase() {
        return DrawStatePhase.DRAW_REQUEST;
    }

    /**
     * 只有异步任务触发的抽奖才需要回写任务状态。
     *
     * @param context 抽奖上下文
     * @return true 表示存在 requestId，需要回写任务
     */
    @Override
    public boolean shouldExecute(DrawContext context) {
        return context.getRequestId() != null;
    }

    /**
     * 保存中奖人员快照并把抽奖任务标记为成功。
     *
     * @param context 抽奖上下文
     */
    @Override
    public void execute(DrawContext context) {
        // 1. 把中奖记录转换为任务结果快照
        String resultJson = JacksonUtil.writeObjectAsString(buildWinners(context.getWinningRecords()));

        // 2. 只更新仍处于处理中的任务，避免覆盖其他终态
        int updated = drawRequestMapper.update(null,
                new LambdaUpdateWrapper<DrawRequestDO>()
                        .eq(DrawRequestDO::getId, context.getRequestId())
                        .eq(DrawRequestDO::getStatus,
                                DrawRequestStatusEnum.PROCESSING.getCode())
                        .set(DrawRequestDO::getStatus,
                                DrawRequestStatusEnum.SUCCESS.getCode())
                        .set(DrawRequestDO::getResultJson, resultJson)
                        .set(DrawRequestDO::getErrorCode, null)
                        .set(DrawRequestDO::getErrorMessage, null));
        // 3. 任务状态必须更新成功，否则抽奖事务整体回滚
        if (updated != 1) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ERROR);
        }
    }

    /**
     * 把中奖记录转换为前端展示的中奖人员列表。
     *
     * @param records 中奖记录
     * @return 中奖人员列表
     */
    private List<DrawWinnerDTO> buildWinners(List<WinningRecordDO> records) {
        // 空记录直接返回，避免创建无意义集合
        if (records == null || records.isEmpty()) {
            return List.of();
        }

        List<DrawWinnerDTO> winners = new ArrayList<>(records.size());
        for (WinningRecordDO record : records) {
            // 只暴露用户 id 和姓名，避免把联系方式写入任务结果
            DrawWinnerDTO winner = new DrawWinnerDTO();
            winner.setUserId(record.getWinnerId());
            winner.setUserName(record.getWinnerName());
            winners.add(winner);
        }
        return winners;
    }
}
