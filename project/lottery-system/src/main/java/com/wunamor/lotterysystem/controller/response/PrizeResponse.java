package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 奖品列表项响应。
 */
@Data
public class PrizeResponse {

    private Long prizeId;

    private String prizeName;

    private String description;

    private BigDecimal price;

    private String imageUrl;
}
