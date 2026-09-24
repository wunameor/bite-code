package com.wunamor.springtransdemo.controller;


import com.wunamor.springtransdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Transactional
    @RequestMapping("/insertUser")
    public String insertUser(String userName, String password) {
        userService.insertUser(userName, password);

        return "执行成功";
    }

    @Transactional
    @RequestMapping("/insertUser2")
    public String insertUser2(String userName, String password) {
        userService.insertUser(userName, password);
        int i = 1/0;
        return "执行成功";
    }

    @Transactional
    @RequestMapping("/insertUser3")
    public String insertUser3(String userName, String password) {
        try {
            userService.insertUser(userName, password);
            int i = 1/0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "执行成功";
    }
}
