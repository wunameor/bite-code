package com.wunamor.bookdemo.model;


import lombok.Data;

@Data
public class PageRequest {
    private Integer currentPage = 1;
    private Integer size = 10;

    private Integer offset;

    public Integer getOffset() {
        return (currentPage - 1) * size;
    }
}
