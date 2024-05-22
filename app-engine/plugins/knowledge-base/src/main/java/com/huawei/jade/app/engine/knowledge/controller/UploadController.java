/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestHeader;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.ReadableBinaryEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.knowledge.dto.FileUploadRequest;
import com.huawei.jade.app.engine.knowledge.service.FileService;

import java.io.IOException;
import java.util.List;

@Component
@RequestMapping("/knowledge")
public class UploadController {
    @Fit
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param fileName 文件名
     * @param knowledgeId 知识库id
     * @param knowledgeTableId 表id
     * @param file 文件
     * @throws IOException 异常
     */
    @PostMapping("/{knowledge_id}/table/{knowledge_table_id}/files")
    public void upload(@RequestHeader(value = "attachment-filename", defaultValue = "blank") String fileName,
        @PathVariable("knowledge_id") Long knowledgeId, @PathVariable("knowledge_table_id") Long knowledgeTableId,
        ReadableBinaryEntity file) throws IOException {
        fileService.importFiles(knowledgeId, knowledgeTableId,
            new FileUploadRequest(file.getInputStream(), fileName));
    }

    /**
     * 删除文件接口
     *
     * @param knowledgeId 知识库id
     * @param knowledgeTableId 表id
     * @param fileNames 文件名
     */
    @DeleteMapping("/{knowledge_id}/table/{knowledge_table_id}/files")
    public void delete(@PathVariable("knowledge_id") Long knowledgeId,
        @PathVariable("knowledge_table_id") Long knowledgeTableId, @RequestBody List<String> fileNames) {
        fileNames.forEach(fileName ->  fileService.deleteFiles(knowledgeId, knowledgeTableId, fileName));
    }
}
