package com.wunamor.springblogdemo.controller;


import com.wunamor.springblogdemo.pojo.dto.UserLoginDTO;
import com.wunamor.springblogdemo.pojo.vo.user.UserInfoVO;
import com.wunamor.springblogdemo.pojo.vo.user.UserLoginVO;
import com.wunamor.springblogdemo.service.UserInfoService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public UserLoginVO login(@Validated @RequestBody UserLoginDTO userInfoLoginDTO) {
        return userInfoService.login(userInfoLoginDTO);
    }

    @GetMapping("/getUserInfo")
    public UserInfoVO getUserInfo(@NotNull Integer userId) {
        return userInfoService.getUserInfo(userId);
    }

    @GetMapping("/getUserInfoByBlogId")
    public UserInfoVO getUserInfoByBlogId(@NotNull Integer blogId) {
        return userInfoService.getUserInfoByBlogId(blogId);
    }
}
