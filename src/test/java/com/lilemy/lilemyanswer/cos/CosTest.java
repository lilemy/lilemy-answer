package com.lilemy.lilemyanswer.cos;

import com.lilemy.lilemyanswer.manager.CosManager;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CosTest {

    @Resource
    private CosManager cosManager;

    @Test
    void putObject() {
        PutObjectResult test = cosManager.putObject("test.jpg", "E:\\code\\lilemy-answer\\lilemy-answer-frontend\\public\\1718868969324.jpg");
        System.out.println(test);
    }
}
