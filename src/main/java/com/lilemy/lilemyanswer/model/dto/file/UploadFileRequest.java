package com.lilemy.lilemyanswer.model.dto.file;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 文件上传请求
 */
@Data
public class UploadFileRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1478777853371773034L;
    /**
     * 业务
     */
    private String biz;

}