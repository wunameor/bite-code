package com.wunamor.springblogdemo.pojo.vo.blog;

import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class BlogDetailVO {
    private Integer id;
    private String title;
    private String content;
    private Integer userId;
    private Date updateTime;

    public static BlogDetailVO create(BlogInfo blogInfo) {
        if (blogInfo == null) {
            return new BlogDetailVO();
        }
        BlogDetailVO blogListResponseVO = new BlogDetailVO();
        BeanUtils.copyProperties(blogInfo, blogListResponseVO);
        return blogListResponseVO;
    }
}
