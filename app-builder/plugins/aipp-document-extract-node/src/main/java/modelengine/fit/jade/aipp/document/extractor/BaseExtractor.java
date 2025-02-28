/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.extractor;

import modelengine.fit.jober.aipp.service.OperatorService.FileType;

import java.util.Map;

/**
 * 表示文件提取器的公共接口。
 *
 * @author 兰宇晨
 * @since 2025-01-06
 */
public interface BaseExtractor {
    /**
     * 表示文件内容提取接口。
     *
     * @param fileUrl 文件链接。
     * @param context 文件提取额外参数。
     * @return 表示文件内容的 {@link String}。
     */
    String extract(String fileUrl, Map<String, Object> context);

    /**
     * 提取器类型提取接口。
     *
     * @return 表示可提取类型的 {@link FileType}
     */
    FileType type();
}
