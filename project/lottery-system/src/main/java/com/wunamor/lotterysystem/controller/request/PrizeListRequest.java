package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 奖品列表查询请求：分页参数。
 */
@Data
public class PrizeListRequest {

    /**
     * 当前页码，从 1 开始
     */
    @Min(value = 1, message = "页码必须大于 0")
    private long currentPage = 1;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数必须大于 0")
    @Max(value = 100, message = "每页最多查询 100 条")
    private long pageSize = 10;
}
