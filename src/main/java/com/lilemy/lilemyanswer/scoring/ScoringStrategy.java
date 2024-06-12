package com.lilemy.lilemyanswer.scoring;


import com.lilemy.lilemyanswer.model.entity.App;
import com.lilemy.lilemyanswer.model.entity.UserAnswer;

import java.util.List;

/**
 * 评分策略
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     *
     * @param choices 回答列表
     * @param app     应用
     * @return {@link UserAnswer}
     */
    UserAnswer doScore(List<String> choices, App app) throws Exception;
}