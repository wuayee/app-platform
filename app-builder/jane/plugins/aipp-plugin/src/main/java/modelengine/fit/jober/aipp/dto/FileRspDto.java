/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 文件传输响应Dto
 *
 * @author: s00664640
 * @since 2024-09-01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileRspDto {
    @Property(name = "file_name")
    private String fileName;

    @Property(name = "file_path")
    private String filePath;

    @Property(name = "file_type")
    private String fileType;
}
