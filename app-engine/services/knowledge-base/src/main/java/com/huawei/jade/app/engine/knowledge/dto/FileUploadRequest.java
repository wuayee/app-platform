/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class FileUploadRequest {
    private InputStream entity;
    private String fileName;
}
