package com.mikeliwei.aistumanage.auth;

import com.mikeliwei.aistumanage.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版 token 存储。
 * 简化设计：应用重启后登录态失效，且不支持多实例部署。
 */
@Component
public class TokenStore {

    /** 默认有效期：2 小时 */
    public static final long DEFAULT_TTL_MILLIS = 2 * 60 * 60 * 1000L;

    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();

    private final long ttlMillis;

    public TokenStore() {
        this(DEFAULT_TTL_MILLIS);
    }

    /** 允许自定义有效期，便于单元测试。 */
    public TokenStore(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public String issue(SysUser user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, new TokenEntry(token, user.getId(), user.getUsername(), user.getRealName(),
                user.getRole(), System.currentTimeMillis() + ttlMillis));
        return token;
    }

    /** 校验 token，不存在或已过期返回 null，并顺带清理过期项。 */
    public TokenEntry get(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        TokenEntry entry = tokens.get(token);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            tokens.remove(token);
            return null;
        }
        return entry;
    }

    public void revoke(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }

    public void clear() {
        tokens.clear();
    }

    public int size() {
        return tokens.size();
    }

    @Data
    @AllArgsConstructor
    public static class TokenEntry {

        private String token;

        private Long userId;

        private String username;

        private String realName;

        private String role;

        /** 过期时间戳（毫秒） */
        private long expireAt;

        public boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }
}
