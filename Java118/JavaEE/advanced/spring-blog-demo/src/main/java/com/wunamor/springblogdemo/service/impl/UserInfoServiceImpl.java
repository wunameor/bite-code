package com.wunamor.springblogdemo.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wunamor.springblogdemo.pojo.entity.UserInfo;
import com.wunamor.springblogdemo.service.UserInfoService;
import com.wunamor.springblogdemo.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author Study
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2026-09-26 10:38:31
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

}




