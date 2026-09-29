package com.mikeliwei.aistumanage.auth;

/**
 * 保存当前请求的登录用户，由 AuthInterceptor 写入、afterCompletion 清理。
 */
public final class AuthContext {

    private static final ThreadLocal<TokenStore.TokenEntry> CURRENT = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(TokenStore.TokenEntry entry) {
        CURRENT.set(entry);
    }

    public static TokenStore.TokenEntry get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
