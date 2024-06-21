package com.lilemy.lilemyanswer.service.impl;

import cn.hutool.core.util.StrUtil;
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
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ai 方法实现
 */
@Slf4j
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
    public SseEmitter aiGenerateQuestionSee(AIGenerateQuestionRequest aiGenerateQuestionRequest) {
        // 获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        // 获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ResultCode.NOT_FOUND_ERROR);
        // 封装 Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        // 建立 SSE 连接对象，0 表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // AI 生成，SSE 流式返回
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest(AIConstant.GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);
        // 左括号计数器，除了默认值外，当回归为 0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);
        // 拼接完整题目
        StringBuilder stringBuilder = new StringBuilder();
        modelDataFlowable
                .observeOn(Schedulers.io())
                .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    List<Character> characterList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        characterList.add(c);
                    }
                    return Flowable.fromIterable(characterList);
                })
                .doOnNext(c -> {
                    // 如果是 '{'，计数器 + 1
                    if (c == '{') {
                        counter.addAndGet(1);
                    }
                    if (counter.get() > 0) {
                        stringBuilder.append(c);
                    }
                    if (c == '}') {
                        counter.addAndGet(-1);
                        if (counter.get() == 0) {
                            // 可以拼接题目，并且通过 SSE 返回给前端
                            sseEmitter.send(JSONUtil.toJsonStr(stringBuilder.toString()));
                            // 重置，准备拼接下一道题
                            stringBuilder.setLength(0);
                        }
                    }
                })
                .doOnError((e) -> log.error("sse error", e))
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
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
