package com.lilemy.lilemyanswer.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedissonClient redissonClient;

    // 分布式锁的 key
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    /**
     * AI 评分结果本地缓存
     */
    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存 5 分钟移除
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, jsonStr);
        String answerJson = answerCacheMap.getIfPresent(cacheKey);
        // 如果有缓存，直接返回
        if (StrUtil.isNotBlank(answerJson)) {
            // 构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        }
        // 定义锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);
        try {
            // 竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            // 没抢到锁，强行返回
            if (!res) {
                return null;
            }
            // 抢到锁了，执行后续业务逻辑
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
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 构建缓存 key
     *
     * @param appId   应用 id
     * @param choices 用户选项列表
     * @return md5 加密
     */
    private String buildCacheKey(Long appId, String choices) {
        return DigestUtil.md5Hex(appId + ":" + choices);
    }
}
