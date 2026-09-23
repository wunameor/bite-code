package com.wunamor.springaopdemo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class TimeAspect {

//    @Around("execution(* com.wunamor.springaopdemo.controller.*.*(..))")
    public Object executeTime(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = point.proceed();
        log.info(point.getSignature() + " 执行时间为：{} ms", System.currentTimeMillis() - start);
        return proceed;
    }
}
