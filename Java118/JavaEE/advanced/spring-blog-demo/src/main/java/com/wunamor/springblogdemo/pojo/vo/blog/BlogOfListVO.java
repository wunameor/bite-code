package com.wunamor.springblogdemo.pojo.vo.blog;

import com.wunamor.springblogdemo.common.constants.Constant;
import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class BlogOfListVO {
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

    public static BlogOfListVO create(BlogInfo blogInfo) {
        if (blogInfo == null) {
            return new BlogOfListVO();
        }
        BlogOfListVO blogOfListVO = new BlogOfListVO();
        BeanUtils.copyProperties(blogInfo, blogOfListVO);
        return blogOfListVO;
    }
}
