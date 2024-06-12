package com.lilemy.lilemyanswer.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lilemy.lilemyanswer.model.dto.scoringresult.ScoringResultQueryRequest;
import com.lilemy.lilemyanswer.model.entity.ScoringResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lilemy.lilemyanswer.model.vo.scoringresult.ScoringResultVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lilemy
 * @description 针对表【scoring_result(评分结果)】的数据库操作Service
 * @createDate 2024-06-12 11:21:10
 */
public interface ScoringResultService extends IService<ScoringResult> {

    /**
     * @param scoringResult 评分结果对象
     * @param add           true - 新建请求
     */
    void validScoringResult(ScoringResult scoringResult, boolean add);

    /**
     * @param scoringResultId 评分结果id
     * @param request         用户登录态
     */
    void isOwnScoringResult(Long scoringResultId, HttpServletRequest request);

    /**
     * @param scoringResult 评分结果对象
     * @param request       用户登录态
     * @return {@link ScoringResultVO}
     */
    ScoringResultVO getScoringResultVO(ScoringResult scoringResult, HttpServletRequest request);

    /**
     * @param scoringResultQueryRequest 评分结果对象分页请求
     * @return 评分结果对象分页条件
     */
    Wrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringResultQueryRequest);

    /**
     * @param scoringResultPage 评分结果对象分页
     * @param request           用户登录态
     * @return {@link Page<ScoringResultVO>}
     */
    Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringResultPage, HttpServletRequest request);
}
