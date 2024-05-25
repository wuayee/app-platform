/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import com.huawei.jade.app.engine.knowledge.utils.DecodeUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class FileUploadRequest {
    private InputStream entity;

    private String fileName;

    /**
     * 获取文件名称
     *
     * @return 解码后的文件名称
     */
    public String getFileName() {
        return DecodeUtil.decodeStr(fileName);
    }
}
