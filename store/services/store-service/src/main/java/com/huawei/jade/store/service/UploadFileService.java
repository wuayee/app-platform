/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.store.entity.parser.MethodEntity;

import java.util.List;

/**
 * 文件上传接口类。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-18
 */
public interface UploadFileService {
    /**
     * 表示上传文件的接口。
     *
     * @param fileName 表示上传的文件名的 {@link String}。
     * @param receiveFile 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @return 返回上传文件中的 JSON schema 数据列表的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.tool.file.upload")
    List<MethodEntity> fileUpload(String fileName, PartitionedEntity receiveFile);
}
