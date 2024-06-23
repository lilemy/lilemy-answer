package com.lilemy.lilemyanswer.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lilemy.lilemyanswer.common.ResultCode;
import com.lilemy.lilemyanswer.constant.CommonConstant;
import com.lilemy.lilemyanswer.exception.BusinessException;
import com.lilemy.lilemyanswer.exception.ThrowUtils;
import com.lilemy.lilemyanswer.model.dto.useranswer.UserAnswerAddRequest;
import com.lilemy.lilemyanswer.model.dto.useranswer.UserAnswerQueryRequest;
import com.lilemy.lilemyanswer.model.entity.App;
import com.lilemy.lilemyanswer.model.entity.User;
import com.lilemy.lilemyanswer.model.entity.UserAnswer;
import com.lilemy.lilemyanswer.model.enums.ReviewStatusEnum;
import com.lilemy.lilemyanswer.model.vo.user.UserVO;
import com.lilemy.lilemyanswer.model.vo.useranswer.UserAnswerVO;
import com.lilemy.lilemyanswer.scoring.ScoringStrategyExecutor;
import com.lilemy.lilemyanswer.service.AppService;
import com.lilemy.lilemyanswer.service.UserAnswerService;
import com.lilemy.lilemyanswer.mapper.UserAnswerMapper;
import com.lilemy.lilemyanswer.service.UserService;
import com.lilemy.lilemyanswer.util.SqlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lilemy
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Service实现
 * @createDate 2024-06-12 11:21:11
 */
@Slf4j
@Service
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer>
        implements UserAnswerService {

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Resource
    private ScoringStrategyExecutor scoringStrategyExecutor;

    @Override
    public void validUserAnswer(UserAnswer userAnswer, boolean add) {
        ThrowUtils.throwIf(userAnswer == null, ResultCode.PARAMS_ERROR);
        // 从对象中取值
        Long appId = userAnswer.getAppId();
        Long id = userAnswer.getId();
        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(appId == null || appId <= 0, ResultCode.PARAMS_ERROR, "appId 非法");
            ThrowUtils.throwIf(id == null || id <= 0, ResultCode.PARAMS_ERROR, "id 不存在");
        }
        // 修改数据时，有参数则校验
        // 补充校验规则
        ThrowUtils.throwIf(appId == null || appService.getById(appId) == null, ResultCode.PARAMS_ERROR, "应用不存在");
    }

    @Override
    public Long createUserAnswer(UserAnswerAddRequest userAnswerAddRequest, HttpServletRequest request) {
        // 在此处将实体类和 DTO 进行转换
        UserAnswer userAnswer = new UserAnswer();
        BeanUtils.copyProperties(userAnswerAddRequest, userAnswer);
        List<String> choices = userAnswerAddRequest.getChoices();
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        // 数据校验
        this.validUserAnswer(userAnswer, true);
        // 判断 app 是否存在
        Long appId = userAnswerAddRequest.getAppId();
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ResultCode.NOT_FOUND_ERROR);
        if (!ReviewStatusEnum.PASS.equals(ReviewStatusEnum.getEnumByValue(app.getReviewStatus()))) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR, "应用未通过审核，无法答题");
        }
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        userAnswer.setUserId(loginUser.getId());
        // 写入数据库
        try {
            boolean result = this.save(userAnswer);
            ThrowUtils.throwIf(!result, ResultCode.OPERATION_ERROR);
        } catch (DuplicateKeyException e) {
            // ignore error
        }
        // 返回新写入的数据 id
        long newUserAnswerId = userAnswer.getId();
        // 调用评分模块
        try {
            UserAnswer userAnswerWithResult = scoringStrategyExecutor.doScore(choices, app);
            userAnswerWithResult.setId(newUserAnswerId);
            userAnswerWithResult.setAppId(null);
            this.updateById(userAnswerWithResult);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new BusinessException(ResultCode.OPERATION_ERROR, "评分错误");
        }
        return newUserAnswerId;
    }

    @Override
    public UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request) {
        // 对象转封装类
        UserAnswerVO userAnswerVO = UserAnswerVO.objToVo(userAnswer);
        // 关联查询用户信息
        Long userId = userAnswer.getUserId();
        UserVO userVO = userService.getUserVO(userId);
        userAnswerVO.setUser(userVO);
        return userAnswerVO;
    }

    @Override
    public Wrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest) {
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (userAnswerQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = userAnswerQueryRequest.getId();
        Long appId = userAnswerQueryRequest.getAppId();
        Integer appType = userAnswerQueryRequest.getAppType();
        Integer scoringStrategy = userAnswerQueryRequest.getScoringStrategy();
        String choices = userAnswerQueryRequest.getChoices();
        Long resultId = userAnswerQueryRequest.getResultId();
        String resultName = userAnswerQueryRequest.getResultName();
        String resultDesc = userAnswerQueryRequest.getResultDesc();
        String resultPicture = userAnswerQueryRequest.getResultPicture();
        Integer resultScore = userAnswerQueryRequest.getResultScore();
        Long userId = userAnswerQueryRequest.getUserId();
        Long notId = userAnswerQueryRequest.getNotId();
        String searchText = userAnswerQueryRequest.getSearchText();
        String sortField = userAnswerQueryRequest.getSortField();
        String sortOrder = userAnswerQueryRequest.getSortOrder();
        String underlineSortField = StrUtil.toUnderlineCase(sortField);
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("result_name", searchText).or().like("result_desc", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(choices), "choices", choices);
        queryWrapper.like(StringUtils.isNotBlank(resultName), "result_name", resultName);
        queryWrapper.like(StringUtils.isNotBlank(resultDesc), "result_desc", resultDesc);
        queryWrapper.like(StringUtils.isNotBlank(resultPicture), "result_picture", resultPicture);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultId), "result_id", resultId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "app_id", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appType), "app_type", appType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultScore), "result_score", resultScore);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringStrategy), "scoring_strategy", scoringStrategy);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                underlineSortField);
        return queryWrapper;
    }

    @Override
    public Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage) {
        List<UserAnswer> userAnswerList = userAnswerPage.getRecords();
        Page<UserAnswerVO> userAnswerVOPage = new Page<>(userAnswerPage.getCurrent(), userAnswerPage.getSize(), userAnswerPage.getTotal());
        if (CollUtil.isEmpty(userAnswerList)) {
            return userAnswerVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserAnswerVO> userAnswerVOList = userAnswerList.stream().map(UserAnswerVO::objToVo).collect(Collectors.toList());
        // 关联查询用户信息
        Set<Long> userIdSet = userAnswerList.stream().map(UserAnswer::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        userAnswerVOList.forEach(userAnswerVO -> {
            Long userId = userAnswerVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            userAnswerVO.setUser(userService.getUserVO(user));
        });
        userAnswerVOPage.setRecords(userAnswerVOList);
        return userAnswerVOPage;
    }
}




