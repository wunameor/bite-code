package com.wunamor.lotterysystem.controller.advice;

import com.wunamor.lotterysystem.common.pojo.CommonResult;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    /** 只接管本项目的 Controller */
    private static final String CONTROLLER_PACKAGE = "com.wunamor.lotterysystem.controller";

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        // ① 只包装自己的 controller 包（排除 actuator / springdoc / 第三方 starter 的接口）
        if (!returnType.getContainingClass().getPackageName().startsWith(CONTROLLER_PACKAGE)) {
            return false;
        }
        // ② 允许用注解显式关闭
        if (returnType.hasMethodAnnotation(IgnoreResponseWrap.class)
                || returnType.getContainingClass().isAnnotationPresent(IgnoreResponseWrap.class)) {
            return false;
        }
        // ③ 这些返回类型一律不碰
        Class<?> type = returnType.getParameterType();
        if (CommonResult.class.isAssignableFrom(type)      // 已经包过 / 异常处理器的返回值
                || CharSequence.class.isAssignableFrom(type)   // String：最关键的一条
                || type == byte[].class                        // 文件流
                || Resource.class.isAssignableFrom(type)
                || ProblemDetail.class.isAssignableFrom(type)  // RFC 9457
                || HttpEntity.class.isAssignableFrom(type)     // 含 ResponseEntity
                || type == void.class) {
            return false;
        }
        // ④ 只处理 JSON（Jackson 3）。若以后引入 Jackson 2，
        //    这里要换成下方注释里的“按类名比较”写法。
        return converterType == JacksonJsonHttpMessageConverter.class;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof CommonResult<?>) {
            return body;
        }
        return CommonResult.success(body);
    }
}
