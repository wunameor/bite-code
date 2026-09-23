package com.wunamor.springaopdemo.controller;


import com.wunamor.springaopdemo.aop.TimeCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/custom")
@RestController
public class CustomController {

    @TimeCustom
    @RequestMapping("/t1")
    public void t1() {
        log.info("custom t1...");
    }
}
