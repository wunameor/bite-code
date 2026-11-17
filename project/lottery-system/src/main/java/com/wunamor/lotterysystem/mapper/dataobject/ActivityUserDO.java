package com.wunamor.lotterysystem.mapper.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动人员关联数据库对象，对应 activity_user 表。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("activity_user")
public class ActivityUserDO extends BaseDO {

    /**
     * 活动 id
     */
    private Long activityId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户名，落库时以数据库为准，不信任前端传值
     */
    private String userName;

    /**
     * 活动人员状态，取 ActivityUserStatusEnum 的 code
     */
    private String status;
}
