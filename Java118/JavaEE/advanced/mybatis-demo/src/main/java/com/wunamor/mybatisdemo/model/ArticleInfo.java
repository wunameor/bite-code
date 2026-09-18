package com.wunamor.mybatisdemo.model;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleInfo {

    private int id;
    private String title;
    private String content;
    private int uid;
    private int deleteFlag;
    private Date createTime;
    private Date updateTime;

    private String username; // 实际中，这个一般是放在 VO 里面
}
