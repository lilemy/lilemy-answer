package com.lilemy.lilemyanswer.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.lilemy.lilemyanswer.model.dto.user.UserQueryRequest;
import com.lilemy.lilemyanswer.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilemy.lilemyanswer.model.vo.user.LoginUserVO;
import com.lilemy.lilemyanswer.model.vo.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author qq233
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-06-12 11:21:11
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      用户登录态
     * @return 脱敏用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户退出登录
     *
     * @param request 用户登录态
     * @return 是否退出登录成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request 用户登录态
     * @return true - 是管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户信息
     * @return true - 是管理员
     */
    boolean isAdmin(User user);

    /**
     * 获取当前登录用户
     *
     * @param request 用户登录态
     * @return 用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏登录用户信息
     *
     * @param user 用户
     * @return 登录用户 - 脱敏
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏用户信息
     *
     * @param user 用户
     * @return 用户 - 脱敏
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList 用户列表
     * @return 脱敏用户列表
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 查询请求体
     * @return 查询Wrapper
     */
    Wrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
