package com.lilemy.lilemyanswer.model.vo.question;

import cn.hutool.json.JSONUtil;
import com.lilemy.lilemyanswer.model.dto.question.QuestionContentRequest;
import com.lilemy.lilemyanswer.model.entity.Question;
import com.lilemy.lilemyanswer.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目视图
 */
@Data
public class QuestionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1352499351628955487L;
    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentRequest> questionContent;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param questionVO 题目封装类
     * @return {@link Question}
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<QuestionContentRequest> questionContentDTO = questionVO.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContentDTO));
        return question;
    }

    /**
     * 对象转封装类
     *
     * @param question 题目对象
     * @return {@link QuestionVO}
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        String questionContent = question.getQuestionContent();
        if (questionContent != null) {
            questionVO.setQuestionContent(JSONUtil.toList(questionContent, QuestionContentRequest.class));
        }
        return questionVO;
    }
}
