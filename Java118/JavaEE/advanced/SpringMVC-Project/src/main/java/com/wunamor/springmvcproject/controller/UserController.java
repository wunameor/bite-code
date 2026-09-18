package com.wunamor.springmvcproject.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {
    @PostMapping("/login")
    public Boolean login(String userName, String password, HttpSession session) {
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
            return false;
        }

        // TODO 这里是采用硬编码
//        if ("admin".equals(userName) && "admin".equals(password)) {
        if ("admin".equals(password)) {
            // 这里的 key（第一个参数） 一般是抽取出来放在常量里面
            session.setAttribute("userName", userName);

            return true;
        }

        // 用户名/密码 错误
        return false;

    }

    @GetMapping("/getLoginUser")
    public String getLoginUser(HttpSession session) {
        return session.getAttribute("userName").toString();
    }
}
