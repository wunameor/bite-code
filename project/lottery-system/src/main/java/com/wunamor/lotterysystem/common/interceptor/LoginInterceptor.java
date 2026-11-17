package com.wunamor.lotterysystem.common.interceptor;


import com.wunamor.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import com.wunamor.lotterysystem.common.pojo.CommonResult;
import com.wunamor.lotterysystem.common.utils.JWTUtil;
import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    /** 仅管理员可访问的接口 */
    private static final Set<String> ADMIN_ONLY_PATHS = Set.of(
            "/user/register", "/user/list", "/file/upload",
            "/prize/create", "/prize/list",
            "/activity/create", "/activity/list");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS 预检请求不携带 token，直接放行
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // 静态资源（含浏览器自动请求的 /.well-known、*.map 等）不参与登录校验
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            log.debug("请求未携带 token，请求路径 {}", request.getRequestURI());
            writeUnauthorized(response);
            return false;
        }

        Claims claims = JWTUtil.parseJWT(token);
        if (claims == null) {
            log.warn("令牌无效或已过期，请求路径 {}", request.getRequestURI());
            writeUnauthorized(response);
            return false;
        }

        // 注册、人员列表、文件上传、创建奖品、奖品列表、活动接口只允许管理员访问
        if (isAdminOnlyPath(request.getRequestURI()) && !isAdmin(claims)) {
            log.warn("非管理员尝试访问 {}，userId: {}", request.getRequestURI(), claims.get("userId"));
            writeForbidden(response);
            return false;
        }

        return true;
    }

    private boolean isAdmin(Claims claims) {
        Object identity = claims.get("identity");
        return identity instanceof Number && ((Number) identity).intValue() == 0;
    }

    /**
     * 支持 /file/upload/{scene} 这种带子路径的接口
     */
    private boolean isAdminOnlyPath(String uri) {
        return ADMIN_ONLY_PATHS.stream()
                .anyMatch(path -> uri.equals(path) || uri.startsWith(path + "/"));
    }

    /**
     * 兼容三种携带方式：标准 Authorization: Bearer、token、user_token（旧模板）。
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)) {
            if (authorization.startsWith(BEARER_PREFIX)) {
                return authorization.substring(BEARER_PREFIX.length()).trim();
            }
            return authorization.trim();
        }

        String token = request.getHeader("token");
        if (StringUtils.hasText(token)) {
            return token;
        }

        return request.getHeader("user_token");
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JacksonUtil.writeObjectAsString(
                CommonResult.error(GlobalErrorCodeConstants.UNAUTHORIZED)));
    }

    private void writeForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JacksonUtil.writeObjectAsString(
                CommonResult.error(GlobalErrorCodeConstants.FORBIDDEN)));
    }
}
