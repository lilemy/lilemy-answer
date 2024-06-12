package com.lilemy.lilemyanswer.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 审核请求
 */
@Data
public class ReviewRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5115967790361042235L;
    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

}