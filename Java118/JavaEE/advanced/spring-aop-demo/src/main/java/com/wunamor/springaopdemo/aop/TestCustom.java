package com.wunamor.springaopdemo.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class TestCustom {

    @Before("@annotation(com.wunamor.springaopdemo.aop.TimeCustom)")
    public void before() {
        log.info("custom before");
    }

    @After("@annotation(com.wunamor.springaopdemo.aop.TimeCustom)")
    public void after() {
        log.info("custom after");
    }
}
