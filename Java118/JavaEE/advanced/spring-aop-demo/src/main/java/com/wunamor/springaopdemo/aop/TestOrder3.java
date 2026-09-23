package com.wunamor.springaopdemo.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(1)
public class TestOrder3 {
//    @Around("com.wunamor.springaopdemo.aop.TestAspect.pc()")
    public Object order1(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("order before 333");
        Object object = joinPoint.proceed();
        log.info("order after 333");
        return object;
    }
}
