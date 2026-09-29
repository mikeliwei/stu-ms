package com.mikeliwei.aistumanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mikeliwei.aistumanage.auth.TokenStore;
import com.mikeliwei.aistumanage.common.BusinessException;
import com.mikeliwei.aistumanage.dto.LoginRequest;
import com.mikeliwei.aistumanage.entity.SysUser;
import com.mikeliwei.aistumanage.mapper.SysUserMapper;
import com.mikeliwei.aistumanage.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户登录相关业务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;

    private final TokenStore tokenStore;

    /**
     * 登录校验。按需求密码为明文直接比较，不做任何加密处理。
     */
    public LoginVO login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("该账号已被禁用，请联系管理员");
        }
        String token = tokenStore.issue(user);
        log.info("用户登录成功: {}", user.getUsername());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        return vo;
    }

    public void logout(String token) {
        tokenStore.revoke(token);
    }

    /** 把登录态信息转换为返回给前端的结构。 */
    public LoginVO describe(TokenStore.TokenEntry entry) {
        if (entry == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        LoginVO vo = new LoginVO();
        vo.setToken(entry.getToken());
        vo.setUserId(entry.getUserId());
        vo.setUsername(entry.getUsername());
        vo.setRealName(entry.getRealName());
        vo.setRole(entry.getRole());
        return vo;
    }
}
