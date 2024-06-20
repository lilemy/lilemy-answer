package com.lilemy.lilemyanswer.service;

import com.lilemy.lilemyanswer.model.dto.ai.AIGenerateQuestionRequest;
import com.lilemy.lilemyanswer.model.dto.question.QuestionContentRequest;
import com.lilemy.lilemyanswer.model.entity.App;

import java.util.List;

/**
 * ai 相关方法
 */
public interface AIService {

    /**
     * ai 应用题目生成
     *
     * @param aiGenerateQuestionRequest ai 生成题目请求体
     * @return {@link List< QuestionContentRequest >}
     */
    List<QuestionContentRequest> aiGenerateQuestion(AIGenerateQuestionRequest aiGenerateQuestionRequest);

    /**
     * 生成题目的用户消息
     *
     * @param app            应用
     * @param questionNumber 题目数量
     * @param optionNumber   选项数量
     * @return 用户消息
     */
    String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber);

    /**
     * AI 评分用户消息封装
     *
     * @param app                    应用
     * @param questionContentDTOList 题目列表
     * @param choices                用户答案
     * @return 用户消息
     */
    String getAiTestScoringUserMessage(App app, List<QuestionContentRequest> questionContentDTOList, List<String> choices);
}
