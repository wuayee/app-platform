/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.service;

import modelengine.fit.jade.aipp.s3.file.entity.S3FileMetaEntity;

import java.io.File;
import java.io.InputStream;

/**
 * 表示 S3 文件上传服务。
 *
 * @author 兰宇晨
 * @since 2024-12-27
 */
public interface S3Service {
    /**
     * 上传文件到S3.
     *
     * @param stream 表示文件对象流的 {@link InputStream}。
     * @param length 表示文件对象长度的 {@code long}。
     * @param fileName 表示文件名的 {@link String}。
     * @return 表示s3文件元信息实体的 {@link S3FileMetaEntity}。
     */
    S3FileMetaEntity upload(InputStream stream, long length, String fileName);

    /**
     * 从S3下载文件.
     *
     * @param fileUrl 表示文件链接的 {@link String}。
     * @return 表示下载保存临时文件的 {@link String}。
     */
    File download(String fileUrl);
}

