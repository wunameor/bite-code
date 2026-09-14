package com.wunamor.springiocdemo.controller;


import com.wunamor.springiocdemo.model.User;
import com.wunamor.springiocdemo.server.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping("/user")
    public User user() {
        return demoService.user();
    }

    @RequestMapping("/test")
    public String test() {
        System.out.println("this is a test");
        return "test";
    }
}
