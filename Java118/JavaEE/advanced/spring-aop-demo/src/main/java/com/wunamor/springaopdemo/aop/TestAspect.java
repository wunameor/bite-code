package com.wunamor.springaopdemo.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TestAspect {

    @Pointcut("execution(* com.wunamor.springaopdemo.controller.*.*(..))")
    public void pc() {}


//    @Around("pc()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        log.info("around 执行前");
        Object proceed = null;
        try {
            log.info("方法执行前 (Before)");
            proceed = point.proceed();
            log.info("方法执行后 (After)");
            log.info("返回结果");
            log.info("around 执行后");
            return proceed;

        } catch (Throwable throwable) {
            log.error("throwable: ", throwable);
            log.info("AfterThrowing...");
        } finally {
            log.info("afterReturning...");
        }
        return proceed;
    }

//    @Before("pc()")
    public void before() {
        log.info("before 执行");
    }

//    @After("pc()")
    public void after() {
        log.info("after 执行");
    }

//    @AfterReturning("pc()")
    public void afterReturning() {
        log.info("afterReturning 执行");
    }

//    @AfterThrowing("pc()")
    public void afterThrowing() {
        log.info("afterThrowing 执行");
    }
}
