package com.wunamor.bookdemo.service;

import com.wunamor.bookdemo.mapper.UserMapper;
import com.wunamor.bookdemo.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Boolean login(String name, String password) {
        UserInfo user = userMapper.getUserByUserName(name);
        if (user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}
