package com.wunamor.lotterysystem.common.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询基类：Controller 请求和 Service 命令共用。
 */
@Data
public class PageQuery {

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
