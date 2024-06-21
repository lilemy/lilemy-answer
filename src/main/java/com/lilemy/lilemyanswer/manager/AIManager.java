package com.lilemy.lilemyanswer.manager;

import com.lilemy.lilemyanswer.common.ResultCode;
import com.lilemy.lilemyanswer.constant.AIConstant;
import com.lilemy.lilemyanswer.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用 AI 调用能力
 */
@Component
public class AIManager {

    @Resource
    private ClientV4 clientV4;

    /**
     * 同步请求（答案不稳定）
     *
     * @param systemMessage 系统提示信息
     * @param userMessage   用户提示信息
     * @return ai 生成结果
     */
    public String doSyncUnstableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, AIConstant.UNSTABLE_TEMPERATURE);
    }

    /**
     * 同步请求（答案较稳定）
     *
     * @param systemMessage 系统提示信息
     * @param userMessage   用户提示信息
     * @return ai 生成结果
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, AIConstant.STABLE_TEMPERATURE);
    }

    /**
     * 同步请求
     *
     * @param systemMessage 系统提示信息
     * @param userMessage   用户提示信息
     * @param temperature   输出的随机性
     * @return ai 生成结果
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    /**
     * 通用请求（简化消息传递）
     *
     * @param systemMessage 系统提示信息
     * @param userMessage   用户提示信息
     * @param stream        调用方式 (false - 同步)
     * @param temperature   输出的随机性
     * @return ai 生成结果
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessageList.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(userChatMessage);
        return doRequest(chatMessageList, stream, temperature);
    }

    /**
     * 通用流式请求
     *
     * @param messages    提示信息
     * @param temperature 输出的随机性
     * @return ai 生成结果
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> messages, Float temperature) {
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        ModelApiResponse modelApiResponse = clientV4.invokeModelApi(chatCompletionRequest);
        return modelApiResponse.getFlowable();
    }

    /**
     * 通用流式请求（简化消息传递）
     *
     * @param systemMessage 系统提示信息
     * @param userMessage   用户提示信息
     * @param temperature   输出的随机性
     * @return ai 生成结果
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage, String userMessage, Float temperature) {
        // 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(systemChatMessage);
        messages.add(userChatMessage);
        return doStreamRequest(messages, temperature);
    }

    /**
     * 通用请求
     *
     * @param messages    提示信息
     * @param stream      调用方式 (false - 同步)
     * @param temperature 输出的随机性
     * @return ai 生成结果
     */
    public String doRequest(List<ChatMessage> messages, Boolean stream, Float temperature) {
        // 构建请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ResultCode.SYSTEM_ERROR, e.getMessage());
        }
    }
}
