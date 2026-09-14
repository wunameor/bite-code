package com.wunamor.springiocdemo.config;

import com.wunamor.springiocdemo.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {
    @Bean
    public User u1() {
        return new User(1, "zhangsan");
    }
//    @Bean
//    public User u2() {
//        return new User(2, "lisi");
//    }
}
