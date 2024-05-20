/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.controller;

import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestHeader;
import com.huawei.fit.http.entity.ReadableBinaryEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.knowledge.dto.FileUploadRequest;
import com.huawei.jade.app.engine.knowledge.service.FileService;

import java.io.IOException;

@Component
public class UploadController {
    @Fit
    private FileService uploadService;

    @PostMapping("/{knowledge_id}/table/{knowledge_table_id}/files")
    public void upload(@RequestHeader(value = "attachment-filename", defaultValue = "blank") String fileName,
        @PathVariable("knowledge_id") Long knowledgeId, @PathVariable("knowledge_table_id") Long knowledgeTableId,
        ReadableBinaryEntity file) throws IOException {
        uploadService.importFiles(knowledgeId, knowledgeTableId,
            new FileUploadRequest(file.getInputStream(), fileName));
    }
}
