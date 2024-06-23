package com.lilemy.lilemyanswer.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilemy.lilemyanswer.annotation.AuthCheck;
import com.lilemy.lilemyanswer.common.BaseResponse;
import com.lilemy.lilemyanswer.common.DeleteRequest;
import com.lilemy.lilemyanswer.common.ResultCode;
import com.lilemy.lilemyanswer.common.ResultUtils;
import com.lilemy.lilemyanswer.constant.UserConstant;
import com.lilemy.lilemyanswer.exception.BusinessException;
import com.lilemy.lilemyanswer.exception.ThrowUtils;
import com.lilemy.lilemyanswer.model.dto.useranswer.UserAnswerAddRequest;
import com.lilemy.lilemyanswer.model.dto.useranswer.UserAnswerQueryRequest;
import com.lilemy.lilemyanswer.model.entity.User;
import com.lilemy.lilemyanswer.model.entity.UserAnswer;
import com.lilemy.lilemyanswer.model.vo.useranswer.UserAnswerVO;
import com.lilemy.lilemyanswer.service.UserAnswerService;
import com.lilemy.lilemyanswer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "UserAnswerController")
@RequestMapping("/userAnswer")
public class UserAnswerController {

    @Resource
    private UserAnswerService userAnswerService;

    @Resource
    private UserService userService;

    @Operation(summary = "创建用户答案")
    @PostMapping("/add")
    public BaseResponse<Long> addUserAnswer(@RequestBody UserAnswerAddRequest userAnswerAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userAnswerAddRequest == null, ResultCode.PARAMS_ERROR);
        Long newUserAnswerId = userAnswerService.createUserAnswer(userAnswerAddRequest, request);
        return ResultUtils.success(newUserAnswerId);
    }

    @Operation(summary = "删除用户答案")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserAnswer(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserAnswer oldUserAnswer = userAnswerService.getById(id);
        ThrowUtils.throwIf(oldUserAnswer == null, ResultCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserAnswer.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = userAnswerService.removeById(id);
        ThrowUtils.throwIf(!result, ResultCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @Operation(summary = "根据 id 获取用户答案（封装类）")
    @GetMapping("/get/vo")
    public BaseResponse<UserAnswerVO> getUserAnswerVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ResultCode.PARAMS_ERROR);
        // 查询数据库
        UserAnswer userAnswer = userAnswerService.getById(id);
        ThrowUtils.throwIf(userAnswer == null, ResultCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(userAnswerService.getUserAnswerVO(userAnswer, request));
    }

    @Operation(summary = "分页获取用户答案列表（仅管理员可用）")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserAnswer>> listUserAnswerByPage(@RequestBody UserAnswerQueryRequest userAnswerQueryRequest) {
        long current = userAnswerQueryRequest.getCurrent();
        long size = userAnswerQueryRequest.getPageSize();
        // 查询数据库
        Page<UserAnswer> userAnswerPage = userAnswerService.page(new Page<>(current, size),
                userAnswerService.getQueryWrapper(userAnswerQueryRequest));
        return ResultUtils.success(userAnswerPage);
    }

    @Operation(summary = "分页获取用户答案列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserAnswerVO>> listUserAnswerVOByPage(@RequestBody UserAnswerQueryRequest userAnswerQueryRequest) {
        long current = userAnswerQueryRequest.getCurrent();
        long size = userAnswerQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ResultCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserAnswer> userAnswerPage = userAnswerService.page(new Page<>(current, size),
                userAnswerService.getQueryWrapper(userAnswerQueryRequest));
        // 获取封装类
        return ResultUtils.success(userAnswerService.getUserAnswerVOPage(userAnswerPage));
    }

    @Operation(summary = "分页获取当前登录用户创建的用户答案列表")
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<UserAnswerVO>> listMyUserAnswerVOByPage(@RequestBody UserAnswerQueryRequest userAnswerQueryRequest,
                                                                     HttpServletRequest request) {
        ThrowUtils.throwIf(userAnswerQueryRequest == null, ResultCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        userAnswerQueryRequest.setUserId(loginUser.getId());
        return listUserAnswerVOByPage(userAnswerQueryRequest);
    }

    @Operation(summary = "获取答题 id")
    @GetMapping("/generate/id")
    public BaseResponse<Long> generateUserAnswerId() {
        return ResultUtils.success(IdUtil.getSnowflakeNextId());
    }

}
