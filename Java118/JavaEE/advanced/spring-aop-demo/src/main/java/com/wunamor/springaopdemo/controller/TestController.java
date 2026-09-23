package com.wunamor.springaopdemo.controller;


import com.wunamor.springaopdemo.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/t1")
    public String t1() {
        return "t1";
    }

    @RequestMapping("/t2")
    public int t2() {
        return 10;
    }

    @RequestMapping("/e1")
    public int e1() {
        return 10/0;
    }

    @RequestMapping("/json")
    public User json() {
        return new User(10, "lisi");
    }
}
