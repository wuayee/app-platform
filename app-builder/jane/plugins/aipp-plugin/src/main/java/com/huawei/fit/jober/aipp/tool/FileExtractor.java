/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import com.huawei.fitframework.annotation.Genericable;

import java.io.File;

/**
 * 文件内容提取
 *
 * @author 孙怡菲 s00664640
 * @since 2024-06-08
 */
public interface FileExtractor {
    @Genericable("com.huawei.fit.jober.aipp.tool.file.extractor")
    String extractFile(File file);
}
