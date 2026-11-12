package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 奖品列表项。
 */
@Data
public class PrizeDTO {

    /**
     * 奖品 id
     */
    private Long prizeId;

    /**
     * 奖品名称
     */
    private String prizeName;

    /**
     * 奖品描述
     */
    private String description;

    /**
     * 奖品价格
     */
    private BigDecimal price;

    /**
     * 奖品图片相对 URL
     */
    private String imageUrl;
}
