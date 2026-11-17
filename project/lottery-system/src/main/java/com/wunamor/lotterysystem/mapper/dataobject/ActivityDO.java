package com.wunamor.lotterysystem.mapper.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动数据库对象，对应 activity 表。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("activity")
public class ActivityDO extends BaseDO {

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动状态，取 ActivityStatusEnum 的 code
     */
    private String status;
}
