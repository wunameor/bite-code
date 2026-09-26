package com.wunamor.springblogdemo.common.advice;


import com.wunamor.springblogdemo.pojo.response.Result;
import com.wunamor.springblogdemo.common.exception.BlogException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public Result<?> exception(Exception e) {
        log.error("出现异常, e: ", e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler
    public Result<?> exception(BlogException e) {
        log.error("博客系统出现异常, e: ", e);
        return Result.fail(e.getMsg());
    }
}
