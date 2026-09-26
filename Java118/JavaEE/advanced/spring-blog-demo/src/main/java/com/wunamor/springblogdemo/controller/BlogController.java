package com.wunamor.springblogdemo.controller;

import com.wunamor.springblogdemo.pojo.vo.blog.BlogDetailVO;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogOfListVO;
import com.wunamor.springblogdemo.service.BlogInfoService;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogInfoService blogInfoService;

    @GetMapping("/getList")
    public List<BlogOfListVO> getList() {
        return blogInfoService.getList();
    }

    @GetMapping("/getBlogDetail")
    public BlogDetailVO getBlogDetail(@Nonnull() Integer blogId) {
        log.info("获取博客详情：blogId = {}", blogId);
//        if (blogId == null) {
//            throw new BlogException("获取博客详情失败：博客id不能为空");
//        }

        return blogInfoService.getBlogDetail(blogId);
    }
}
