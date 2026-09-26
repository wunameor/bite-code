package com.wunamor.springblogdemo.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import com.wunamor.springblogdemo.service.BlogInfoService;
import com.wunamor.springblogdemo.mapper.BlogInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author Study
* @description 针对表【blog_info(博客表)】的数据库操作Service实现
* @createDate 2026-09-26 10:38:31
*/
@Service
public class BlogInfoServiceImpl extends ServiceImpl<BlogInfoMapper, BlogInfo>
    implements BlogInfoService{

}




