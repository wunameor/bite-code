package com.wunamor.bookdemo.controller;

import com.wunamor.bookdemo.service.BookService;
import com.wunamor.bookdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Boolean login(String name, String password) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(password)) {
            return false;
        }

        return userService.login(name, password);
    }
}
