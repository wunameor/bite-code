package com.wunamor.lotterysystem.service.command;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建奖品命令：Service 层入参
 */
@Data
public class PrizeCreateCommand {

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
}
