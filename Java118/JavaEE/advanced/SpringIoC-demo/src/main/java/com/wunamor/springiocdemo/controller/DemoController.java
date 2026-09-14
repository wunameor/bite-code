package com.wunamor.springiocdemo.controller;


import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    public String test() {
        System.out.println("this is a test");
        return "test";
    }
}
