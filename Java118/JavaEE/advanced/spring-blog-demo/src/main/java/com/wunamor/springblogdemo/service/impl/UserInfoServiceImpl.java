package com.wunamor.springblogdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wunamor.springblogdemo.common.constants.Constant;
import com.wunamor.springblogdemo.common.enums.ResultCodeEnums;
import com.wunamor.springblogdemo.common.exception.BlogException;
import com.wunamor.springblogdemo.common.utils.JwtUtil;
import com.wunamor.springblogdemo.common.utils.SecurityUtil;
import com.wunamor.springblogdemo.mapper.BlogInfoMapper;
import com.wunamor.springblogdemo.pojo.dto.UserLoginDTO;
import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import com.wunamor.springblogdemo.pojo.entity.UserInfo;
import com.wunamor.springblogdemo.pojo.vo.user.UserInfoVO;
import com.wunamor.springblogdemo.pojo.vo.user.UserLoginVO;
import com.wunamor.springblogdemo.service.BlogInfoService;
import com.wunamor.springblogdemo.service.UserInfoService;
import com.wunamor.springblogdemo.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author Study
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2026-09-26 10:38:31
*/
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private BlogInfoService blogInfoService;

    @Override
    public UserLoginVO login(UserLoginDTO userInfoLoginDTO) {
        String userName = userInfoLoginDTO.getUserName();

        UserInfo userInfo = selectByUserName(userName);
        if (userInfo == null) {
            throw new BlogException(ResultCodeEnums.USER_USER_NAME_ERROR);
        }

//        if (!userInfoLoginDTO.getPassword().equals(userInfo.getPassword())) {
//            throw new BlogException(ResultCodeEnums.USER_PASSWORD_ERROR);
//        }

        if (!SecurityUtil.verify(userInfoLoginDTO.getPassword(), userInfo.getPassword())) {
            throw new BlogException(ResultCodeEnums.USER_PASSWORD_ERROR);
        }

        // 成功登录
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.USER_ID_JWT_KEY, userInfo.getId());
        map.put(Constant.USER_USER_NAME_JWT_KEY, userInfo.getUserName());
        String jwt = JwtUtil.getJwt(map);
        UserLoginVO userLoginVO = new UserLoginVO(userInfo.getId(), jwt);

        return userLoginVO;
    }

    @Override
    public UserInfoVO getUserInfo(Integer userId) {
        return UserInfoVO.create(selectByUserId(userId));
    }

    @Override
    public UserInfoVO getUserInfoByBlogId(Integer blogId) {
        BlogInfo blogInfo = blogInfoService.selectByBlogId(blogId);
        if (blogInfo == null) {
            log.warn("不能找到博客信息：blogId: {}", blogId);
            return new UserInfoVO();
        }

        return UserInfoVO.create(selectByUserId(blogInfo.getUserId()));
    }

    public UserInfo selectByUserId(Integer userId) {
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, userId)
                .eq(UserInfo::getDeleteFlag, Constant.NOT_DELETED));
    }

    public UserInfo selectByUserName(String userName) {
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserName, userName)
                .eq(UserInfo::getDeleteFlag, Constant.NOT_DELETED));
    }
}




