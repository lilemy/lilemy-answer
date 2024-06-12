package com.lilemy.lilemyanswer.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilemy.lilemyanswer.model.dto.app.AppQueryRequest;
import com.lilemy.lilemyanswer.model.entity.App;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilemy.lilemyanswer.model.vo.app.AppVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lilemy
 * @description 针对表【app(应用)】的数据库操作Service
 * @createDate 2024-06-12 11:21:10
 */
public interface AppService extends IService<App> {

    /**
     * 参数校验
     *
     * @param app 应用对象
     * @param add   是否为添加请求
     */
    void validApp(App app, boolean add);

    /**
     * 是否对应用具有操作权限
     *
     * @param appId   应用id
     * @param request 用户登录态
     */
    void isOwnApp(long appId, HttpServletRequest request);

    /**
     * 获取应用封装
     *
     * @param app     应用对象
     * @param request 登录态
     * @return {@link AppVO}
     */
    AppVO getAppVO(App app, HttpServletRequest request);

    /**
     * 获取分页查询条件
     *
     * @param appQueryRequest 分页查询
     * @return 查询条件
     */
    Wrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取应用分页列表封装
     *
     * @param appPage 应用分页列表
     * @param request 用户登录态
     * @return {@link Page<AppVO>}
     */
    Page<AppVO> getAppVOPage(Page<App> appPage, HttpServletRequest request);

}
