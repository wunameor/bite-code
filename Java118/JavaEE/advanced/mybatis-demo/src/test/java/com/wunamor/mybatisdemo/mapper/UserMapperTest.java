package com.wunamor.mybatisdemo.mapper;

import com.wunamor.mybatisdemo.model.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void getList() {
        List<UserInfo> list = userMapper.getList();
        System.out.println(list.toString());
    }

    @Test
    void getList2() {
        System.out.println(userMapper.getList2());
    }

    @Test
    void getListById() {
        System.out.println(userMapper.getListById(1));
    }

    @Test
    void getUserByUsernameAndPassword() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("zhangsan");
        userInfo.setPassword("zhangsan");
        System.out.println(userMapper.getUserByUsernameAndPassword(userInfo));
    }

    @Test
    void getUserByUsernameAndPassword2() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("zhangsan");
        userInfo.setPassword("zhangsan");
        System.out.println(userMapper.getUserByUsernameAndPassword2(userInfo));
    }

    @Test
    void insertUser() {
        for (int i = 0; i < 5; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername("lisi");
            userInfo.setPassword("lisi");
            userInfo.setAge(19);
            System.out.println(userMapper.insertUser(userInfo));
            System.out.println("userInfo id: " + userInfo.getId());
        }
    }

    @Test
    void deleteUserById() {
        System.out.println(userMapper.deleteUserById(6));
    }

    @Test
    void updateUserById() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("77777dwa");
        userInfo.setId(5);
        System.out.println(userMapper.updateUserById(userInfo));
    }

    @Test
    void getUserByLikeName() {
        System.out.println(userMapper.getUserByLikeName("lisi"));
    }
}
