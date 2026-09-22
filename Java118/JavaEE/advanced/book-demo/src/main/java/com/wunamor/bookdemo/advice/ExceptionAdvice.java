package com.wunamor.bookdemo.advice;


import com.wunamor.bookdemo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public Result<?> exception(Exception e) {
        log.error("出现异常：e: ", e);
        return Result.fail("系统内部错误，请联系管理员");
    }

    @ExceptionHandler
    public Result<?> exception(NullPointerException e) {
        log.error("出现异常：e: ", e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler
    public Result<?> exception(ArithmeticException e) {
        log.error("出现异常：e: ", e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler
    public Result<?> exception(NoResourceFoundException e) {
        log.error("出现异常：e: ", e);
        return Result.fail(e.getMessage());
    }
}
