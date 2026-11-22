package com.wunamor.lotterysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.WinningRecordMapper;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import com.wunamor.lotterysystem.service.WinningRecordService;
import com.wunamor.lotterysystem.service.dto.WinningRecordDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 中奖记录查询服务实现。
 */
@Service
public class WinningRecordServiceImpl implements WinningRecordService {

    private final WinningRecordMapper winningRecordMapper;

    public WinningRecordServiceImpl(WinningRecordMapper winningRecordMapper) {
        this.winningRecordMapper = winningRecordMapper;
    }

    @Override
    public List<WinningRecordDTO> show(Long activityId, Long prizeId) {
        // 1. 活动 id 是查询中奖记录的最小条件
        if (activityId == null) {
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_ID_IS_EMPTY);
        }

        // 2. 按活动查询，可选按奖品过滤，按中奖时间升序展示
        LambdaQueryWrapper<WinningRecordDO> wrapper = new LambdaQueryWrapper<WinningRecordDO>()
                .eq(WinningRecordDO::getActivityId, activityId)
                .eq(prizeId != null, WinningRecordDO::getPrizeId, prizeId)
                .orderByAsc(WinningRecordDO::getWinningTime)
                .orderByAsc(WinningRecordDO::getId);
        List<WinningRecordDO> records = winningRecordMapper.selectList(wrapper);

        // 3. DO 转 DTO
        List<WinningRecordDTO> result = new ArrayList<>(records.size());
        for (WinningRecordDO record : records) {
            result.add(convert(record));
        }
        return result;
    }

    private WinningRecordDTO convert(WinningRecordDO record) {
        WinningRecordDTO dto = new WinningRecordDTO();
        dto.setActivityId(record.getActivityId());
        dto.setPrizeId(record.getPrizeId());
        dto.setPrizeName(record.getPrizeName());
        dto.setPrizeTier(record.getPrizeTier());
        PrizeTierEnum tier = PrizeTierEnum.getByCode(record.getPrizeTier());
        dto.setPrizeTierName(tier == null ? record.getPrizeTier() : tier.getDesc());
        dto.setWinnerId(record.getWinnerId());
        dto.setWinnerName(record.getWinnerName());
        dto.setWinningTime(record.getWinningTime());
        return dto;
    }
}
