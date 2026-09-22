package com.wunamor.bookdemo.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/v1")
    public int v1() {
        return 10;
    }

    @RequestMapping("/v2")
    public boolean v2() {
        return true;
    }

    @RequestMapping("/v3")
    public String v3() {
        return "v3";
    }

    @RequestMapping("/v4")
    public void v4() {
        int a = 1/0;
    }

    @RequestMapping("/v5")
    public void v5() {
        String s = null;
        s.getBytes();
    }


}
