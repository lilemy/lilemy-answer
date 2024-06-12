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

    void validUserAnswer(UserAnswer userAnswer, boolean add);

    Long createUserAnswer(UserAnswerAddRequest userAnswerAddRequest, HttpServletRequest request);

    UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request);

    Wrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest);

    Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request);


}
