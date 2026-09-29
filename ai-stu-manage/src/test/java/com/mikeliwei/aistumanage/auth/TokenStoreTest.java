package com.mikeliwei.aistumanage.auth;

import com.mikeliwei.aistumanage.entity.SysUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * TokenStore 单元测试，不依赖 Spring 容器与数据库。
 */
class TokenStoreTest {

    private static SysUser user() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setRealName("系统管理员");
        user.setRole("ADMIN");
        user.setStatus(1);
        return user;
    }

    @Test
    @DisplayName("签发后可以取回登录信息")
    void issueAndGetShouldReturnLoginInfo() {
        TokenStore store = new TokenStore();

        String token = store.issue(user());

        assertNotNull(token);
        assertEquals(32, token.length());
        TokenStore.TokenEntry entry = store.get(token);
        assertNotNull(entry);
        assertEquals(1L, entry.getUserId().longValue());
        assertEquals("admin", entry.getUsername());
        assertEquals("系统管理员", entry.getRealName());
        assertEquals("ADMIN", entry.getRole());
        assertEquals(1, store.size());
    }

    @Test
    @DisplayName("未知或空白 token 返回 null")
    void getShouldReturnNullForUnknownOrBlankToken() {
        TokenStore store = new TokenStore();

        assertNull(store.get("not-exists"));
        assertNull(store.get(null));
        assertNull(store.get("   "));
    }

    @Test
    @DisplayName("过期 token 返回 null 并被清理")
    void getShouldReturnNullAndCleanUpAfterExpiry() {
        // ttl = -1 表示签发即过期，避免用 sleep 造成测试不稳定
        TokenStore store = new TokenStore(-1);

        String token = store.issue(user());

        assertNull(store.get(token));
        assertEquals(0, store.size());
    }

    @Test
    @DisplayName("登出后 token 失效")
    void revokeShouldRemoveToken() {
        TokenStore store = new TokenStore();
        String token = store.issue(user());

        store.revoke(token);

        assertNull(store.get(token));
        assertEquals(0, store.size());
        // 传入 null 不应抛异常
        store.revoke(null);
    }

    @Test
    @DisplayName("多次登录签发不同 token")
    void eachLoginShouldProduceDistinctToken() {
        TokenStore store = new TokenStore();

        String first = store.issue(user());
        String second = store.issue(user());

        assertNotEquals(first, second);
        assertEquals(2, store.size());
    }
}
