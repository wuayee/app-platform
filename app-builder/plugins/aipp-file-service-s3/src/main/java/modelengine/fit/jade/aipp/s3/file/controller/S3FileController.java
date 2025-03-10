/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.controller;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.service.annotations.CarverSpan;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.jade.aipp.s3.file.code.S3FileRetCode;
import modelengine.fit.jade.aipp.s3.file.entity.S3FileMetaEntity;
import modelengine.fit.jade.aipp.s3.file.exception.S3FileException;
import modelengine.fit.jade.aipp.s3.file.service.S3Service;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.validation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * S3 文件上传接口。
 *
 * @author 兰宇晨
 * @since 2024-12-18
 */
@Component
@Validated
@RequestMapping(path = "/s3/file", group = "S3 文件上传接口")
public class S3FileController {
    private static final Logger LOG = Logger.get(S3FileController.class);

    private final S3Service s3Service;

    public S3FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * 上传文件。
     *
     * @param receivedFiles 表示上传文件的{@link PartitionedEntity}。
     * @return 表示文件元信息的 {@link List}{@code <}{@link S3FileMetaEntity}{@code >}。
     */
    @CarverSpan(value = "operation.s3file.upload")
    @PostMapping(description = "上传文件")
    public List<S3FileMetaEntity> upload(PartitionedEntity receivedFiles) {
        notNull(receivedFiles, "The file to be uploaded cannot be null.");
        List<NamedEntity> entityList =
                receivedFiles.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (entityList.isEmpty()) {
            LOG.warn("Received file list is empty.");
            return Collections.emptyList();
        }
        for (NamedEntity entity : entityList) {
            String fileName = entity.asFile().filename();
            if (fileName.contains("..")) {
                LOG.warn("File name is invalid: {}.", fileName);
                throw new S3FileException(S3FileRetCode.S3_FILE_NAME_INVALID, fileName);
            }
        }
        List<S3FileMetaEntity> metaEntities = new ArrayList<>();
        for (NamedEntity entity : entityList) {
            FileEntity file = entity.asFile();
            String fileName = file.filename().replace(" ", "");
            LOG.info("Received upload file:{}.", fileName);
            metaEntities.add(this.s3Service.upload(file.getInputStream(), file.length(), fileName));
        }
        return metaEntities;
    }
}
