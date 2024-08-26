/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import modelengine.fitframework.annotation.Genericable;

/**
 * 文件工具类。
 *
 * @author 孙怡菲
 * @since 2024-05-30
 */
public interface FileTool {
    /**
     * 提取文件内容。
     *
     * @param filePath 表示文件路径 {@link String}。
     * @return 表示提取的文件内容 {@link String}。
     */
    @Genericable("com.huawei.fit.jober.aipp.tool.extract.file")
    String extractFile(String filePath);
}
