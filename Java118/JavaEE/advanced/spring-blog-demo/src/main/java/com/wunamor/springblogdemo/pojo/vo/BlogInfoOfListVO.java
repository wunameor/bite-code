package com.wunamor.springblogdemo.pojo.vo;

import com.wunamor.springblogdemo.common.constants.Constant;
import com.wunamor.springblogdemo.common.exception.BlogException;
import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class BlogInfoOfListVO {
    private Integer id;
    private String title;
    private String content;

    private Date updateTime;

    public String getContent() {
        if (content == null) {
            return "";
        }
        return content.length() < Constant.BLOG_SUB_LENGTH ? content : content.substring(Constant.BLOG_SUB_LENGTH);
    }

    public static BlogInfoOfListVO create(BlogInfo blogInfo) {
        if (blogInfo == null) {
            return new BlogInfoOfListVO();
        }
        BlogInfoOfListVO blogInfoOfListVO = new BlogInfoOfListVO();
        BeanUtils.copyProperties(blogInfo, blogInfoOfListVO);
        return blogInfoOfListVO;
    }
}
