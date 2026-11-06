package com.wunamor.lotterysystem.mapper;

import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void emailCountTest() {
        System.out.println(userMapper.getUserCountByEmail("12@qq.com"));
    }

    @Test
    void phoneCountTest() {
        System.out.println(userMapper.getUserCountByPhone(new Encrypt("13809883812")));
    }
}
