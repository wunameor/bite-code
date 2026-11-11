package com.wunamor.lotterysystem.controller.advice;


import com.wunamor.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ControllerException;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ControllerException.class)
    public CommonResult<?> controllerException(ControllerException e) {
        log.error("controllerException：" + e);
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public CommonResult<?> serviceException(ServiceException e) {
        log.error("serviceException：" + e);
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    /** @Validated 校验失败（@RequestBody + @Validated） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining("；"));
        log.error("参数校验失败：{}", message);
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),
                message.isEmpty() ? GlobalErrorCodeConstants.BAD_REQUEST.getMessage() : message);
    }

    /** 请求体不是合法 JSON / 类型不匹配 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("请求体解析失败：{}", e.getMessage());
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST);
    }

    /** 静态资源不存在（含浏览器自动请求的 *.map、/.well-known 等） */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResult<?>> noResourceFoundException(NoResourceFoundException e) {
        log.debug("静态资源不存在：{}", e.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResult.error(GlobalErrorCodeConstants.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<?> exception(Exception e) {
        log.error("系统出现异常：" + e);
        return CommonResult.error(GlobalErrorCodeConstants.ERROR);
    }
}
