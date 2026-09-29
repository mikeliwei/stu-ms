package com.mikeliwei.aistumanage.controller;

import com.mikeliwei.aistumanage.auth.AuthContext;
import com.mikeliwei.aistumanage.auth.TokenStore;
import com.mikeliwei.aistumanage.common.Result;
import com.mikeliwei.aistumanage.dto.LoginRequest;
import com.mikeliwei.aistumanage.service.AuthService;
import com.mikeliwei.aistumanage.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录模块接口。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 登录，唯一不需要 token 的接口 */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        TokenStore.TokenEntry current = AuthContext.get();
        if (current != null) {
            authService.logout(current.getToken());
        }
        return Result.ok();
    }

    /** 获取当前登录用户信息，供前端刷新页面后恢复状态 */
    @GetMapping("/me")
    public Result<LoginVO> me() {
        return Result.ok(authService.describe(AuthContext.get()));
    }
}
