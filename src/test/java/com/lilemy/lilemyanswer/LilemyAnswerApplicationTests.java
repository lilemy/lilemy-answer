package com.lilemy.lilemyanswer;

import com.lilemy.lilemyanswer.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class LilemyAnswerApplicationTests {

    @Test
    void contextLoads() {
        String s = "{\"finish_reason\":\"stop\",\"index\":0,\"message\":{\"content\":\"```json\\n{\\n  \\\"resultName\\\": \\\"内向直觉思维型（INTJ）\\\",\\n  \\\"resultDesc\\\": \\\"根据你的回答，你被评估为内向直觉思维型（INTJ）。你倾向于独立思考，喜欢深入分析和理解复杂的概念。在解决问题时，你倾向于采取逻辑和系统化的方法，而不是依赖情感或直观的反应。你似乎更偏好有计划和组织的生活方式，对时间和规则有较强的意识。在社交场合中，虽然你可能不是最外向的人，但你能够以自己的方式与他人有效沟通。面对新的挑战时，你通常表现出谨慎和策略性，这使你能够在大多数情况下做出明智的决策。总的来说，你的性格类型表明你是一个有远见、善于策划和高度独立的个体。\\\"\\n}\\n```\",\"role\":\"assistant\"},\"delta\":null}";
        String unescapeJava = StringEscapeUtils.unescapeJava(s);
        String startIndex = StringUtils.subStringAssignEnd(unescapeJava, "{", 3);
        int endIndex = startIndex.indexOf("}");
        String substring = startIndex.substring(0, endIndex + 1);
        System.out.println(substring);
    }

}
