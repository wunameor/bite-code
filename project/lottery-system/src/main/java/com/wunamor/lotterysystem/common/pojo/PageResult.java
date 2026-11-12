package com.wunamor.lotterysystem.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页结果。
 *
 * @param <T> 列表项类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 当前页数据
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码，从 1 开始
     */
    private long currentPage;

    /**
     * 每页条数
     */
    private long pageSize;

    /**
     * 总页数由 total 和 pageSize 推导，避免调用方传入不一致的值。
     */
    public long getTotalPages() {
        return pageSize <= 0 ? 0 : (total + pageSize - 1) / pageSize;
    }
}
