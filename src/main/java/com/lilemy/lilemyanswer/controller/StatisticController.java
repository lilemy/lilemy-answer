package com.lilemy.lilemyanswer.controller;

import com.lilemy.lilemyanswer.annotation.AuthCheck;
import com.lilemy.lilemyanswer.common.BaseResponse;
import com.lilemy.lilemyanswer.common.ResultCode;
import com.lilemy.lilemyanswer.common.ResultUtils;
import com.lilemy.lilemyanswer.constant.UserConstant;
import com.lilemy.lilemyanswer.exception.ThrowUtils;
import com.lilemy.lilemyanswer.mapper.UserAnswerMapper;
import com.lilemy.lilemyanswer.model.vo.app.AppVO;
import com.lilemy.lilemyanswer.model.vo.statistic.AppAnswerCountVO;
import com.lilemy.lilemyanswer.model.vo.statistic.AppAnswerResultCountVO;
import com.lilemy.lilemyanswer.model.entity.App;
import com.lilemy.lilemyanswer.model.vo.statistic.AppAnswerResultVO;
import com.lilemy.lilemyanswer.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "StatisticController")
@RequestMapping("/statistic")
public class StatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    @Resource
    private AppService appService;

    @Operation(summary = "热门应用排行")
    @GetMapping("/answer/count")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<AppAnswerCountVO>> getAppAnswerCount() {
        List<AppAnswerCountVO> appAnswerCountList = new ArrayList<>();
        List<AppAnswerCountVO> appAnswerCountRequests = userAnswerMapper.doAppAnswerCount();
        for (AppAnswerCountVO appAnswerCountRequest : appAnswerCountRequests) {
            Long appId = appAnswerCountRequest.getAppId();
            App app = appService.getById(appId);
            appAnswerCountRequest.setApp(AppVO.objToVo(app));
            appAnswerCountList.add(appAnswerCountRequest);
        }
        return ResultUtils.success(appAnswerCountList);
    }

    @Operation(summary = "应用回答分布")
    @GetMapping("/answer/result/count")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppAnswerResultCountVO> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ResultCode.PARAMS_ERROR);
        AppAnswerResultCountVO appAnswerResultCountVO = new AppAnswerResultCountVO();
        List<AppAnswerResultVO> appAnswerResultVOS = userAnswerMapper.doAppAnswerResultCount(appId);
        App app = appService.getById(appId);
        appAnswerResultCountVO.setResultList(appAnswerResultVOS);
        appAnswerResultCountVO.setApp(AppVO.objToVo(app));
        return ResultUtils.success(appAnswerResultCountVO);
    }
}
