package com.lilemy.lilemyanswer.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户创建请求
 */
@Data
public class UserAddRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8899385320220001733L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user, admin
     */
    private String userRole;
}
