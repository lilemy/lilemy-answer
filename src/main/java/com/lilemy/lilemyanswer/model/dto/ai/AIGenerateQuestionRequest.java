package com.lilemy.lilemyanswer.model.dto.ai;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ai 生成题目请求体
 */
@Data
public class AIGenerateQuestionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4244588866261825557L;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 题目数
     */
    int questionNumber = 10;

    /**
     * 选项数
     */
    int optionNumber = 2;
}
