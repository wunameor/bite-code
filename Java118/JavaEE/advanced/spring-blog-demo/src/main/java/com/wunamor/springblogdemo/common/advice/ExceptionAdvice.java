package com.wunamor.springblogdemo.common.advice;


import com.wunamor.springblogdemo.common.pojo.response.Result;
import com.wunamor.springblogdemo.common.exception.BlogException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public Result<?> handle(Exception e) {
        log.error("出现异常, e: ", e);
        return Result.fail("系统内部异常，请联系管理员");
    }

    @ExceptionHandler
    public Result<?> handle(BlogException e) {
        log.error("博客系统出现异常, e: ", e);
        return Result.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler
    public Result<?> handle(MethodArgumentNotValidException e) {
        log.error("博客系统出现异常, e: ", e);
        return Result.fail(e.getMessage());
    }
}
