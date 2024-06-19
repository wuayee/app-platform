/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestHeader;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.store.entity.parser.MethodEntity;
import com.huawei.jade.store.service.UploadFileService;

import java.util.List;

/**
 * 表示上传文件的控制器。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-18
 */
@Component
@RequestMapping("/tools")
public class UploadFileController {
    private final UploadFileService uploadFileService;

    /**
     * 通过上传文件服务来初始化 {@link UploadFileController} 的新实例。
     *
     * @param uploadFileService 表示上传文件服务的 {@link UploadFileService}。
     */
    public UploadFileController(UploadFileService uploadFileService) {
        this.uploadFileService = notNull(uploadFileService, "The tool upload service cannot be null.");
    }

    /**
     * 表示上传文件的请求。
     *
     * @param fileName 表示上传的文件名的 {@link String}。
     * @param receivedFile 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @return 返回上传文件中的 JSON schema 数据列表的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     */
    @PostMapping(path = "/file", description = "上传文件")
    public List<MethodEntity> uploadFile(
            @RequestHeader(value = "tool-filename", defaultValue = "blank") String fileName,
            PartitionedEntity receivedFile) {
        return this.uploadFileService.fileUpload(fileName, receivedFile);
    }
}
