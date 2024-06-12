package com.lilemy.lilemyanswer.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilemy.lilemyanswer.model.dto.question.QuestionQueryRequest;
import com.lilemy.lilemyanswer.model.dto.question.QuestionUpdateRequest;
import com.lilemy.lilemyanswer.model.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilemy.lilemyanswer.model.vo.question.QuestionVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lilemy
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2024-06-12 11:21:10
 */
public interface QuestionService extends IService<Question> {

    /**
     * 参数校验
     *
     * @param question 题目对象
     * @param add      是否为添加请求
     */
    void validQuestion(Question question, boolean add);

    /**
     * 是否对题目具有操作权限
     *
     * @param questionId 题目id
     * @param request    用户登录态
     */
    void isOwnQuestion(long questionId, HttpServletRequest request);

    /**
     * 获取题目封装
     *
     * @param question 题目对象
     * @param request  登录态
     * @return {@link QuestionVO}
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 获取分页查询条件
     *
     * @param questionQueryRequest 分页查询
     * @return 查询条件
     */
    Wrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目分页列表封装
     *
     * @param questionPage 题目分页列表
     * @param request      用户登录态
     * @return {@link Page<QuestionVO>}
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

}
