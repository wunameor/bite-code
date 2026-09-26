package com.wunamor.springblogdemo.service;

import com.wunamor.springblogdemo.pojo.dto.UserLoginDTO;
import com.wunamor.springblogdemo.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wunamor.springblogdemo.pojo.vo.user.UserInfoVO;
import com.wunamor.springblogdemo.pojo.vo.user.UserLoginVO;
import jakarta.validation.constraints.NotNull;

/**
* @author Study
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2026-09-26 10:38:31
*/
public interface UserInfoService extends IService<UserInfo> {

    UserLoginVO login(UserLoginDTO userInfoLoginDTO);

    UserInfoVO getUserInfo(@NotNull Integer userId);

    UserInfoVO getUserInfoByBlogId(@NotNull Integer blogId);
}
