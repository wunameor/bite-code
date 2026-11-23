package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 活动详情中的奖品快照。
 */
@Data
public class ActivityDetailPrizeDTO {

    private Long prizeId;

    /**
     * 奖品名称，对应抽奖页面的 data.name
     */
    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Long prizeAmount;

    /**
     * 奖品等级 code，例如 FIRST_PRIZE
     */
    private String prizeTier;

    /**
     * 奖品等级中文名，例如 一等奖
     */
    private String prizeTierName;

    /**
     * 是否待抽取：true 未抽，false 已抽
     */
    private boolean valid;

    /**
     * 中奖名单：来自 winning_record，随活动详情快照一起写入 Redis
     */
    private List<ActivityDetailUserDTO> list;
}
