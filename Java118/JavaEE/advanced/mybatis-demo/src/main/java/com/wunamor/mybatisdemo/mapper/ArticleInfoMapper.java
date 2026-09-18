package com.wunamor.mybatisdemo.mapper;


import com.wunamor.mybatisdemo.model.ArticleInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleInfoMapper {
    public ArticleInfo getArticleInfoById(Integer id);
}
