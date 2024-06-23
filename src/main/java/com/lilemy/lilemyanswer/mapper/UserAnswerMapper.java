package com.lilemy.lilemyanswer.mapper;

import com.lilemy.lilemyanswer.model.vo.statistic.AppAnswerCountVO;
import com.lilemy.lilemyanswer.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lilemy.lilemyanswer.model.vo.statistic.AppAnswerResultVO;

import java.util.List;

/**
 * @author lilemy
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2024-06-12 11:21:11
 * @Entity com.lilemy.lilemyanswer.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    /**
     * 统计热门应用排行
     *
     * @return {@link List<AppAnswerCountVO>}
     */
    List<AppAnswerCountVO> doAppAnswerCount();

    /**
     * 应用回答分布
     *
     * @param appId 应用 id
     * @return {@link List<AppAnswerResultVO>}
     */
    List<AppAnswerResultVO> doAppAnswerResultCount(Long appId);
}




