package com.lilemy.lilemyanswer.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5082642364398416473L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}
