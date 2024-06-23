package com.lilemy.lilemyanswer.model.vo.statistic;

import com.lilemy.lilemyanswer.model.vo.app.AppVO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * App 用户提交答案书统计
 */
@Data
public class AppAnswerCountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8692197320171699989L;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 用户提交答案数
     */
    private Long answerCount;

    /**
     * 应用
     */
    private AppVO app;
}