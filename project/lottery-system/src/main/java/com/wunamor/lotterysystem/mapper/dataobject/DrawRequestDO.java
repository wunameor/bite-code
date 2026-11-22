package com.wunamor.lotterysystem.mapper.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 抽奖异步任务数据库对象，对应 draw_request 表。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("draw_request")
public class DrawRequestDO extends BaseDO {

    /**
     * 活动 id
     */
    private Long activityId;

    /**
     * 奖品 id
     */
    private Long prizeId;

    /**
     * 请求中的中奖人 id 列表，JSON 数组
     */
    private String winnerIds;

    /**
     * 任务状态，取 DrawRequestStatusEnum 的 code
     */
    private String status;

    /**
     * 失败错误码
     */
    private Integer errorCode;

    /**
     * 失败原因
     */
    private String errorMessage;

    /**
     * 成功后的中奖人员快照，JSON 数组
     */
    private String resultJson;

    /**
     * 补偿重试次数
     */
    private Integer retryCount;
}
