package com.wunamor.springiocdemo;

import com.wunamor.springiocdemo.controller.DemoController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringIoCDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringIoCDemoApplication.class, args);
//        ConfigurableApplicationContext context = SpringApplication.run(SpringIoCDemoApplication.class, args);

//        DemoController bean1 = context.getBean(DemoController.class);
//        bean1.test();
//        System.out.println(bean1);
//
//        // 除去开头为连续大写字母，其他的是用小驼峰命名
//        DemoController bean2 = (DemoController)context.getBean("demoController");
//        bean2.test();
//        System.out.println(bean2);
//
//        // 一般是用名称 + 类对象来获取 bean 对象
//        DemoController bean3 = context.getBean("demoController", DemoController.class);
//        bean3.test();
//        System.out.println(bean3);

    }

}
