package com.wunamor.springiocdemo.server;

import com.wunamor.springiocdemo.model.User;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    // 通过 resource 注入
//    @Resource(name = "u1")
//    private User user;

    // 通过 Autowired 注入, 如果是容器内有多个 Bean 一般使用 qualifier 来指定名称
//    @Autowired
//    @Qualifier("u2")
//    private User user;


    // 构造方法注入，如果是有无参的构造方法，那么建议就需要使用 @Autowired 来注入
//    private User user;
//
//    public DemoService() {
//    }
//
//    @Autowired
//    public DemoService(User user) {
//        this.user = user;
//    }

    // 通过 set 方式注入
    private User user;

    @Autowired
    public void setUser(User user) {
        this.user = user;
    }

    public User user() {
        return user;
    }
}
