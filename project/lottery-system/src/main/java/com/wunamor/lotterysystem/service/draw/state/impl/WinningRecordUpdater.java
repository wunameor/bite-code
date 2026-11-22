package com.wunamor.lotterysystem.service.draw.state.impl;

import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.WinningRecordMapper;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import com.wunamor.lotterysystem.service.draw.state.DrawContext;
import com.wunamor.lotterysystem.service.draw.state.DrawStatePhase;
import com.wunamor.lotterysystem.service.draw.state.DrawStateUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 中奖记录写入步骤。
 *
 * <p>批量写入中奖记录，并校验写入行数与上下文中的中奖人数一致。</p>
 */
@Slf4j
@Component
public class WinningRecordUpdater implements DrawStateUpdater {

    /** 中奖记录 Mapper，用于批量写入中奖记录。 */
    private final WinningRecordMapper winningRecordMapper;

    /**
     * 构造中奖记录写入步骤。
     *
     * @param winningRecordMapper 中奖记录 Mapper
     */
    public WinningRecordUpdater(WinningRecordMapper winningRecordMapper) {
        this.winningRecordMapper = winningRecordMapper;
    }

    /**
     * 返回中奖记录阶段，作为抽奖状态翻转的第一步。
     *
     * @return 中奖记录阶段
     */
    @Override
    public DrawStatePhase phase() {
        return DrawStatePhase.RECORD;
    }

    /**
     * 批量写入中奖记录；写入不完整时回滚整个抽奖事务。
     *
     * @param context 抽奖上下文
     */
    @Override
    public void execute(DrawContext context) {
        // 1. 中奖记录是写入中奖表的必要条件
        List<WinningRecordDO> records = context.getWinningRecords();
        if (records == null || records.isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_INFO_IS_EMPTY);
        }

        try {
            // 2. 批量写入，并校验写入行数与上下文一致
            int inserted = winningRecordMapper.insertBatch(records);
            if (inserted != records.size()) {
                log.error("中奖记录写入行数不一致，expected: {}, actual: {}",
                        records.size(), inserted);
                throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ERROR);
            }
        } catch (ServiceException e) {
            // 3. 保留原始业务错误码
            throw e;
        } catch (RuntimeException e) {
            // 4. 数据库等技术异常统一转换，由外层事务整体回滚
            log.error("中奖记录批量写入失败，recordCount: {}", records.size(), e);
            throw new ServiceException(ServiceErrorCodeConstants.DRAW_PRIZE_ERROR);
        }
    }
}
