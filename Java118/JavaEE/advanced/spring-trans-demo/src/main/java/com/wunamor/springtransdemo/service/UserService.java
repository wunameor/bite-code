package com.wunamor.springtransdemo.service;


import com.wunamor.springtransdemo.mapper.UserInfoMapper;
import com.wunamor.springtransdemo.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Transactional(propagation = Propagation.NESTED)
    public void insertUser(String userName, String password) {
        UserInfo userInfo = new UserInfo(userName, password);
        userInfoMapper.insertUser(userInfo);
    }
}
