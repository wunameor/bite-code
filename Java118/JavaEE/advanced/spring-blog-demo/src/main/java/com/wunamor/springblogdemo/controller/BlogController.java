package com.wunamor.springblogdemo.controller;

import com.wunamor.springblogdemo.pojo.dto.BlogUpDTO;
import com.wunamor.springblogdemo.pojo.dto.BlogUpdateDTO;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogDetailVO;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogOfListVO;
import com.wunamor.springblogdemo.service.BlogInfoService;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public Boolean add(@Validated @RequestBody BlogUpDTO blogUpDTO) {
        log.info("博客添加：blog: {}", blogUpDTO);
        return blogInfoService.add(blogUpDTO);
    }

    @PostMapping("/delete")
    public Boolean delete(@NotNull Integer blogId) {
        // TODO 这里还有一个很大的 bug，删除的博客的作者需要和 JWT 中的用户一样，可以根据 在 JWT 解析的时候存储起来（使用第三方的组件），然后在 service 层调用判断

        log.info("博客删除：blogId: {}", blogId);
        return blogInfoService.delete(blogId);
    }

    @PostMapping("/update")
    public Boolean update(@Validated @RequestBody BlogUpdateDTO blogUpdateDTO) {
        log.info("博客删除：blogUpdateDTO: {}", blogUpdateDTO);
        return blogInfoService.update(blogUpdateDTO);
    }
}
