package com.lilemy.lilemyanswer.model.vo.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppAnswerResultVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5035567684913954799L;
    // 结果名称
    private String resultName;
    // 对应个数
    private String resultCount;
}
