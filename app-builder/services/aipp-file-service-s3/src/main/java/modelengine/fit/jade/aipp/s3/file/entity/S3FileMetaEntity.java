/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.s3.file.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import modelengine.fitframework.annotation.Property;

/**
 * S3 文件元信息实体。
 *
 * @author 兰宇晨
 * @since 2024-12-18。
 */
@AllArgsConstructor
@Data
public class S3FileMetaEntity {
    /**
     * 文件名称。
     */
    @Property(name = "file_name")
    private String fileName;

    /**
     * 文件连接。
     */
    @Property(name = "file_url")
    private String fileUrl;

    /**
     * 文件类型。
     */
    @Property(name = "file_type")
    private String fileType;
}
