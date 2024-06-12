package com.lilemy.lilemyanswer.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6399649444829597881L;

    private String userAccount;

    private String userPassword;
}
