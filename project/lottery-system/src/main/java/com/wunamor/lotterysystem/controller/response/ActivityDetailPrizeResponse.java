package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 活动详情中的奖品响应。
 */
@Data
public class ActivityDetailPrizeResponse {

    private Long prizeId;

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
     * 中奖名单：来自中奖记录，随活动详情快照一起返回
     */
    private List<ActivityDetailUserResponse> list;
}
