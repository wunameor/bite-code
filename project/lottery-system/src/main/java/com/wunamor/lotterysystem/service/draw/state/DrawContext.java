package com.wunamor.lotterysystem.service.draw.state;

import com.wunamor.lotterysystem.mapper.dataobject.ActivityDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityPrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import com.wunamor.lotterysystem.mapper.dataobject.PrizeDO;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 抽奖状态翻转上下文。
 *
 * <p>只在抽奖主流程和服务内部状态更新器之间传递，不序列化到 MQ 或 HTTP 响应。</p>
 */
@Data
public class DrawContext {

    /**
     * 异步任务 id；同步直调时可以为空
     */
    private Long requestId;

    private Long activityId;

    private Long prizeId;

    private List<Long> winnerIds;

    private ActivityDO activity;

    private ActivityPrizeDO activityPrize;

    private PrizeDO prize;

    private Map<Long, ActivityUserDO> activityUserById;

    private Map<Long, UserDO> userById;

    private List<WinningRecordDO> winningRecords;

    /**
     * 活动是否已经因本次抽奖完成
     */
    private boolean activityFinished;
}
