package com.lilemy.lilemyanswer.model.dto.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 更新题目请求
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5771591534259558605L;
    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentRequest> questionContent;

}