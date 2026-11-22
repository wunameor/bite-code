package com.wunamor.lotterysystem.mapper.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 中奖记录数据库对象，对应 winning_record 表。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("winning_record")
public class WinningRecordDO extends BaseDO {

    /**
     * 活动 id
     */
    private Long activityId;

    /**
     * 活动名称快照
     */
    private String activityName;

    /**
     * 奖品 id
     */
    private Long prizeId;

    /**
     * 奖品名称快照
     */
    private String prizeName;

    /**
     * 奖品等级 code
     */
    private String prizeTier;

    /**
     * 中奖人 id
     */
    private Long winnerId;

    /**
     * 中奖人姓名快照
     */
    private String winnerName;

    /**
     * 中奖人邮箱快照
     */
    private String winnerEmail;

    /**
     * 中奖人手机号快照
     */
    private String winnerPhoneNumber;

    /**
     * 中奖时间
     */
    private Date winningTime;
}
