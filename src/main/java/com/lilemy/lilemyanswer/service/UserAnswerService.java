package com.lilemy.lilemyanswer.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilemy.lilemyanswer.model.dto.useranswer.UserAnswerAddRequest;
import com.lilemy.lilemyanswer.model.dto.useranswer.UserAnswerQueryRequest;
import com.lilemy.lilemyanswer.model.entity.UserAnswer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilemy.lilemyanswer.model.vo.useranswer.UserAnswerVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lilemy
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Service
 * @createDate 2024-06-12 11:21:11
 */
public interface UserAnswerService extends IService<UserAnswer> {

    /**
     * 参数校验
     *
     * @param userAnswer 用户答案参数
     * @param add        true - 新建请求
     */
    void validUserAnswer(UserAnswer userAnswer, boolean add);

    /**
     * 创建用户答案
     *
     * @param userAnswerAddRequest 创建用户答案请求
     * @param request              用户登录态
     * @return 用户答案 id
     */
    Long createUserAnswer(UserAnswerAddRequest userAnswerAddRequest, HttpServletRequest request);

    /**
     * 获取用户答题封装类
     *
     * @param userAnswer 用户答案对象
     * @param request    用户登录态
     * @return 用户答题封装类
     */
    UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request);

    /**
     * 获取用户答案分页参数
     *
     * @param userAnswerQueryRequest 查询用户答案请求
     * @return 用户答案分页参数
     */
    Wrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest);

    /**
     * 获取用户答案分页参数封装类
     *
     * @param userAnswerPage 用户答案分页
     * @return 用户答案分页封装类
     */
    Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage);


}
