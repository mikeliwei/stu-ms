package com.mikeliwei.aistumanage.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 登录拦截器：校验 Authorization: Bearer &lt;token&gt;，失败直接返回 401 JSON。
 * 这里手写 JSON 字符串，避免依赖具体的 JSON 序列化实现。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String UNAUTHORIZED_BODY = "{\"code\":401,\"msg\":\"未登录或登录已过期\",\"data\":null}";

    private final TokenStore tokenStore;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // 放行 CORS 预检请求
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        TokenStore.TokenEntry entry = tokenStore.get(resolveToken(request));
        if (entry == null) {
            writeUnauthorized(response);
            return false;
        }
        AuthContext.set(entry);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length()).trim();
        }
        // 兼容自定义请求头
        String header = request.getHeader("X-Token");
        return StringUtils.hasText(header) ? header.trim() : null;
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(UNAUTHORIZED_BODY);
    }
}
