package com.wunamor.bookdemo.service;

import com.wunamor.bookdemo.constants.Constant;
import com.wunamor.bookdemo.mapper.UserMapper;
import com.wunamor.bookdemo.model.UserInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Boolean login(String name, String password, HttpSession session) {
        UserInfo user = userMapper.getUserByUserName(name);
        if (user.getPassword().equals(password)) {
            // 设置 session
            user.setPassword("");
            session.setAttribute(Constant.USER_LOGIN_INFO, user);
            return true;
        }
        return false;
    }
}
