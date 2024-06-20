package com.lilemy.lilemyanswer.service.impl;

import cn.hutool.json.JSONUtil;
import com.lilemy.lilemyanswer.common.ResultCode;
import com.lilemy.lilemyanswer.constant.AIConstant;
import com.lilemy.lilemyanswer.exception.ThrowUtils;
import com.lilemy.lilemyanswer.manager.AIManager;
import com.lilemy.lilemyanswer.model.dto.ai.AIGenerateQuestionRequest;
import com.lilemy.lilemyanswer.model.dto.ai.AIQuestionAnswerRequest;
import com.lilemy.lilemyanswer.model.dto.question.QuestionContentRequest;
import com.lilemy.lilemyanswer.model.entity.App;
import com.lilemy.lilemyanswer.model.enums.AppTypeEnum;
import com.lilemy.lilemyanswer.service.AIService;
import com.lilemy.lilemyanswer.service.AppService;
import jakarta.annotation.Resource;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ai 方法实现
 */
@Service
public class AIServiceImpl implements AIService {

    @Resource
    private AppService appService;

    @Resource
    private AIManager aiManager;

    @Override
    public List<QuestionContentRequest> aiGenerateQuestion(AIGenerateQuestionRequest aiGenerateQuestionRequest) {
        // 获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        // 获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ResultCode.NOT_FOUND_ERROR);
        // 封装 Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        // AI 生成
        String result = aiManager.doSyncStableRequest(AIConstant.GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage);
        // 截取需要的 JSON 信息
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");
        String json = result.substring(start, end + 1);
        String unescapeJava = StringEscapeUtils.unescapeJava(json);
        return JSONUtil.toList(unescapeJava, QuestionContentRequest.class);
    }

    @Override
    public String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(AppTypeEnum.getEnumByValue(app.getAppType()).getText() + "类").append("\n");
        userMessage.append(questionNumber).append("\n");
        userMessage.append(optionNumber);
        return userMessage.toString();
    }

    @Override
    public String getAiTestScoringUserMessage(App app, List<QuestionContentRequest> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<AIQuestionAnswerRequest> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            AIQuestionAnswerRequest questionAnswerDTO = new AIQuestionAnswerRequest();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }
}
