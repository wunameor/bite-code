package com.wunamor.springblogdemo.service;

import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogDetailVO;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogOfListVO;

import java.util.List;

/**
* @author Study
* @description 针对表【blog_info(博客表)】的数据库操作Service
* @createDate 2026-09-26 10:38:31
*/
public interface BlogInfoService extends IService<BlogInfo> {

    List<BlogOfListVO> getList();

    BlogDetailVO getBlogDetail(Integer blogId);

    BlogInfo selectByBlogId(Integer blogId);
}
