package com.wunamor.springiocdemo.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration // 使用这个注解不能直接在 controller 中返回这个对象，不然会出现返回整个 spring 配置的这个 bug
//@Component
@ConfigurationProperties(prefix = "student")
@Data
public class Student {
    private int age;
    private String name;
    private int gender;
}
