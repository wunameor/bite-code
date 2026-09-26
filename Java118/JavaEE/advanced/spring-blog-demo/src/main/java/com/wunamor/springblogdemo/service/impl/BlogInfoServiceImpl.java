package com.wunamor.springblogdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wunamor.springblogdemo.common.constants.Constant;
import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogDetailVO;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogOfListVO;
import com.wunamor.springblogdemo.service.BlogInfoService;
import com.wunamor.springblogdemo.mapper.BlogInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Study
* @description 针对表【blog_info(博客表)】的数据库操作Service实现
* @createDate 2026-09-26 10:38:31
*/
@Service
public class BlogInfoServiceImpl extends ServiceImpl<BlogInfoMapper, BlogInfo>
    implements BlogInfoService{

    @Autowired
    private BlogInfoMapper blogInfoMapper;

    @Override
    public List<BlogOfListVO> getList() {
        List<BlogInfo> blogInfos = blogInfoMapper.selectList(new LambdaQueryWrapper<BlogInfo>()
                .eq(BlogInfo::getDeleteFlag, Constant.NOT_DELETED)
        );

        return blogInfos.stream().map(BlogOfListVO::create).toList();
    }

    @Override
    public BlogDetailVO getBlogDetail(Integer blogId) {
        BlogInfo blogInfo = blogInfoMapper.selectById(blogId);
        BlogDetailVO blogDetailVO = BlogDetailVO.create(blogInfo);
        return blogDetailVO;
    }

    @Override
    public BlogInfo selectByBlogId(Integer blogId) {
        return blogInfoMapper.selectOne(new LambdaQueryWrapper<BlogInfo>()
                .eq(BlogInfo::getDeleteFlag, Constant.NOT_DELETED)
                .eq(BlogInfo::getId, blogId));
    }
}




