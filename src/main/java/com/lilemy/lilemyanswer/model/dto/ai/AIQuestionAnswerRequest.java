package com.lilemy.lilemyanswer.model.dto.ai;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 题目答案封装类（用于 AI 评分）
 */
@Data
public class AIQuestionAnswerRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -9133435293281335793L;
    /**
     * 题目
     */
    private String title;

    /**
     * 用户答案
     */
    private String userAnswer;
}