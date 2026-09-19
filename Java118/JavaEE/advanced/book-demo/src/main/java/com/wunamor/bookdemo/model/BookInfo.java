package com.wunamor.bookdemo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BookInfo {
    //图书ID
    private Integer id;
    //书名
    private String bookName;
    //作者
    private String author;
    //数量
    private Integer count;
    //定价
    private BigDecimal price;
    //出版社
    private String publish;
    //状态 0-⽆效 1-允许借阅 2-不允许借阅
    private Integer status;
    private Date createTime;
    private Date updateTime;

    // 实际开发中是不使用这样的，这个仅仅是用来表示
    private String statusCN;
}
