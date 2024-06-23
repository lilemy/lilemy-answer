package com.lilemy.lilemyanswer.model.vo.statistic;

import com.lilemy.lilemyanswer.model.vo.app.AppVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * App 答案结果统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppAnswerResultCountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1423259386573881317L;

    // 应用
    private AppVO app;

    private List<AppAnswerResultVO> resultList;
}