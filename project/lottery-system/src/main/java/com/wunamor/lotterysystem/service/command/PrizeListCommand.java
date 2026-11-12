package com.wunamor.lotterysystem.service.command;

import lombok.Data;

/**
 * 奖品列表查询命令：Service 层入参。
 */
@Data
public class PrizeListCommand {

    /**
     * 当前页码，从 1 开始
     */
    private long currentPage = 1;

    /**
     * 每页条数
     */
    private long pageSize = 10;
}
