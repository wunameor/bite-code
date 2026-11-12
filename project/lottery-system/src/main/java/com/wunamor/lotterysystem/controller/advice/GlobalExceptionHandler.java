package com.wunamor.lotterysystem.controller.advice;


import com.wunamor.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ControllerException;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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

    /** @Valid 校验失败（@RequestBody / @ModelAttribute） */
    @ExceptionHandler(BindException.class)
    public CommonResult<?> bindException(BindException e) {
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

    /** multipart 里的 param 不是合法 JSON */
    @ExceptionHandler(JsonParseException.class)
    public CommonResult<?> jsonParseException(JsonParseException e) {
        log.error("JSON 解析失败：{}", e.getMessage());
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST);
    }

    /** 上传文件超过 spring.servlet.multipart.max-file-size */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public CommonResult<?> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("上传文件过大：{}", e.getMessage());
        return CommonResult.error(ServiceErrorCodeConstants.FILE_TOO_LARGE);
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
