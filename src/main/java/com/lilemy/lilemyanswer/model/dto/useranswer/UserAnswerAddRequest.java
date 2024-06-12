package com.lilemy.lilemyanswer.model.dto.useranswer;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建用户答案请求
 */
@Data
public class UserAnswerAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 8725050122554708194L;
    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;

}