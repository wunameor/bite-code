package com.wunamor.lotterysystem.mapper.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动奖品关联数据库对象，对应 activity_prize 表。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("activity_prize")
public class ActivityPrizeDO extends BaseDO {

    /**
     * 活动 id
     */
    private Long activityId;

    /**
     * 奖品 id
     */
    private Long prizeId;

    /**
     * 该奖品在活动中的数量
     */
    private Long prizeAmount;

    /**
     * 奖品等级，取 PrizeTierEnum 的 code
     */
    private String prizeTiers;

    /**
     * 活动奖品状态，取 ActivityPrizeStatusEnum 的 code
     */
    private String status;
}
