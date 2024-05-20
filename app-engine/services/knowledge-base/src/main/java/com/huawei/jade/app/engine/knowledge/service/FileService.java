/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.app.engine.knowledge.dto.FileUploadRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 文件服务接口类
 *
 * @since 2024/05/17
 */
public interface FileService {
    /**
     * 文件导入接口
     *
     * @param knowledgeId 知识库id
     * @param knowledgeTableId 知识表id
     * @param fileUploadRequest 文件上传请求
     * @throws IOException IO异常
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.file.import")
    void importFiles(Long knowledgeId, Long knowledgeTableId, FileUploadRequest fileUploadRequest) throws IOException;

    /**
     * 读取文件接口
     *
     * @param knowledgeId 知识库id
     * @param knowledgeTableId 知识表id
     * @param filename 文件名称
     * @return 文件流
     * @throws IOException IO异常
     * @throws IllegalArgumentException 参数异常
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.file.read")
    ByteArrayInputStream read(Long knowledgeId, Long knowledgeTableId, String filename)
        throws IOException, IllegalArgumentException;

    /**
     * 删除特定知识表文件接口
     *
     * @param knowledgeId 知识库id
     * @param knowledgeTableId 知识表id
     * @param filename 文件名称
     * @return true-删除成功
     * @throws IllegalArgumentException 参数异常
     */
    @Genericable(id = "com.huawei.jade.app.engine.knowledge.service.file.delete")
    boolean deleteFiles(Long knowledgeId, Long knowledgeTableId, String filename) throws IllegalArgumentException;
}
