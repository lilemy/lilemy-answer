package com.lilemy.lilemyanswer.model.dto.useranswer;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑用户答案请求
 */
@Data
public class UserAnswerEditRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 309255033784251806L;
    /**
     * id
     */
    private Long id;


    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;

}