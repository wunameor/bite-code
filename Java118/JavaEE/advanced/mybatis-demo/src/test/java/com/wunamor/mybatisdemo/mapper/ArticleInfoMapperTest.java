package com.wunamor.mybatisdemo.mapper;

import com.wunamor.mybatisdemo.model.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ArticleInfoMapperTest {

    @Autowired
    private ArticleInfoMapper articleInfoMapper;

    @Test
    void getArticleInfoById() {
        System.out.println(articleInfoMapper.getArticleInfoById(1));
    }
}
