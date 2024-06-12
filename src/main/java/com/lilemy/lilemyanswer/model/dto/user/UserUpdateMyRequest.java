package com.lilemy.lilemyanswer.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新个人信息请求
 */
@Data
public class UserUpdateMyRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6687171036178874792L;

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
}
