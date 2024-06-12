package com.lilemy.lilemyanswer.model.dto.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑题目请求
 */
@Data
public class QuestionEditRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5391474126970176726L;
    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentDTO> questionContent;

}