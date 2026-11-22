package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.service.dto.WinningRecordDTO;

import java.util.List;

/**
 * 中奖记录查询服务。
 */
public interface WinningRecordService {

    /**
     * 查询活动的中奖记录；传入 prizeId 时只返回该奖品的中奖记录。
     *
     * @param activityId 活动 id
     * @param prizeId 奖品 id，可为空
     * @return 按中奖时间升序排列的中奖记录
     */
    List<WinningRecordDTO> show(Long activityId, Long prizeId);
}
