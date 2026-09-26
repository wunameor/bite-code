package com.wunamor.springblogdemo.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 博客表
 * @TableName blog_info
 */
@TableName(value ="blog_info")
@Data
public class BlogInfo {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private String content;

    /**
     *
     */
    private Integer userId;

    /**
     *
     */
    private Integer deleteFlag;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;
}
