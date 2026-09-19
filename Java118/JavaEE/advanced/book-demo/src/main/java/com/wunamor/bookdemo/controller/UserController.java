package com.wunamor.bookdemo.controller;

import com.wunamor.bookdemo.service.BookService;
import com.wunamor.bookdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Boolean login(String name, String password) {
        log.info("用户登录：name: {}", name);
        if (!StringUtils.hasText(name) || !StringUtils.hasText(password)) {
            log.warn("用户登录-用户名或密码错误: {}", name);
            return false;
        }

        return userService.login(name, password);
    }
}
