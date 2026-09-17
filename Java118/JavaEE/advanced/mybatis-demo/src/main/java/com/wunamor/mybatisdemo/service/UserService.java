package com.wunamor.mybatisdemo.service;

import com.wunamor.mybatisdemo.mapper.UserMapper;
import com.wunamor.mybatisdemo.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<UserInfo> getList() {
        return userMapper.getList();
    }
}
