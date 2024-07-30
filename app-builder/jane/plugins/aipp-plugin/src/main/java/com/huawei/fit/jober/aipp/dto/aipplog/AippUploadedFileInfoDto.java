/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.aipplog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AIPP上传文件信息DTO
 *
 * @author: s00664640
 * @since: 2024-04-14 16:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AippUploadedFileInfoDto {
    private String aippId;
    private String createUserAccount;
    private String filename;
}
