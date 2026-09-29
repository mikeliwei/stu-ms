package com.mikeliwei.aistumanage.vo;

import lombok.Data;

/**
 * 登录成功后返回给前端的信息（不含密码）。
 */
@Data
public class LoginVO {

    private String token;

    private Long userId;

    private String username;

    private String realName;

    private String role;
}
