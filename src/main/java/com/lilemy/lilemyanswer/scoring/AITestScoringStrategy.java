package com.lilemy.lilemyanswer.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lilemy.lilemyanswer.constant.AIConstant;
import com.lilemy.lilemyanswer.constant.AppConstant;
import com.lilemy.lilemyanswer.manager.AIManager;
import com.lilemy.lilemyanswer.model.dto.question.QuestionContentRequest;
import com.lilemy.lilemyanswer.model.entity.App;
import com.lilemy.lilemyanswer.model.entity.Question;
import com.lilemy.lilemyanswer.model.entity.UserAnswer;
import com.lilemy.lilemyanswer.model.vo.question.QuestionVO;
import com.lilemy.lilemyanswer.service.AIService;
import com.lilemy.lilemyanswer.service.QuestionService;
import com.lilemy.lilemyanswer.util.StringUtils;
import jakarta.annotation.Resource;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

/**
 * AI 测评类应用评分策略
 */
@ScoringStrategyConfig(appType = AppConstant.APP_TYPE_TEST, scoringStrategy = AppConstant.APP_SCORING_AI)
public class AITestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AIManager aiManager;

    @Resource
    private AIService aiService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        Long appId = app.getId();
        // 1. 根据 id 查询到题目
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentRequest> questionContent = questionVO.getQuestionContent();

        // 2. 调用 AI 获取结果
        // 封装 Prompt
        String userMessage = aiService.getAiTestScoringUserMessage(app, questionContent, choices);
        // AI 生成
        String result = aiManager.doSyncStableRequest(AIConstant.AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
        // 截取需要的 JSON 信息
        String unescapeJava = StringEscapeUtils.unescapeJava(result);
        String startStr = StringUtils.subStringAssignEnd(unescapeJava, "{", 3);
        int endIndex = startStr.indexOf("}");
        String substring = startStr.substring(0, endIndex + 1);
        // 3. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = JSONUtil.toBean(substring, UserAnswer.class);
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        return userAnswer;
    }
}
