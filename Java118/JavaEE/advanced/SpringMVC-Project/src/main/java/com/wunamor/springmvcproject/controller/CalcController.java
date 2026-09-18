package com.wunamor.springmvcproject.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/calc")
@RestController
public class CalcController {

    @RequestMapping("/sum")
    public String sum(Integer num1, Integer num2) {
        if (num1 == null || num2 == null) {
            // 后期一般是直接抛异常的，然后统一捕获处理
            return "参数不合法";
        }
        return "计算机计算结果: " + (num1 + num2);
    }

}
