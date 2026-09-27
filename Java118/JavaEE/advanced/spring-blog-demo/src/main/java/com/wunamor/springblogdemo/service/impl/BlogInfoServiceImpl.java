package com.wunamor.springblogdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wunamor.springblogdemo.common.constants.Constant;
import com.wunamor.springblogdemo.pojo.dto.BlogUpDTO;
import com.wunamor.springblogdemo.pojo.dto.BlogUpdateDTO;
import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogDetailVO;
import com.wunamor.springblogdemo.pojo.vo.blog.BlogOfListVO;
import com.wunamor.springblogdemo.service.BlogInfoService;
import com.wunamor.springblogdemo.mapper.BlogInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Study
* @description 针对表【blog_info(博客表)】的数据库操作Service实现
* @createDate 2026-09-26 10:38:31
*/
@Service
@Slf4j
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

    @Override
    public Boolean add(BlogUpDTO blogUpDTO) {
        BlogInfo blogInfo = blogUpDTO.transEntity();
        try {
            return blogInfoMapper.insertOrUpdate(blogInfo);
        } catch (Exception e) {
            log.error("博客添加出现异常：e: ", e);
            return false;
        }
    }

    @Override
    public Boolean update(BlogUpdateDTO blogUpdateDTO) {
        BlogInfo blogInfo = blogUpdateDTO.transEntity();
        try {
            int result = blogInfoMapper.updateById(blogInfo);
            return result == 1;
        } catch (Exception e) {
            log.error("博客添加出现异常：e: ", e);
            return false;
        }
    }

    @Override
    public Boolean delete(Integer blogId) {
        try {
            BlogInfo blogInfo = new BlogInfo();
            blogInfo.setId(blogId);
            blogInfo.setDeleteFlag(Constant.DELETED);
            int result = blogInfoMapper.updateById(blogInfo);
            return result == 1;
        } catch (Exception e) {
            log.error("博客添加出现异常：e: ", e);
            return false;
        }
    }

}




