package com.wunamor.bookdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PageResponse<T> {
    private final int totalCount;
    private List<T> pages;
}
