/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.service;

import modelengine.fit.jade.aipp.document.param.FileExtractionParam;
import modelengine.fitframework.annotation.Genericable;

/**
 * 文档提取节点服务。
 *
 * @author 马朝阳
 * @since 2024-12-12
 */
public interface DocumentExtractService {
    /**
     * 文档提取。
     *
     * @param param 表示文件提取信息的 {@link FileExtractionParam}。
     * @return 表示提取结果的 {@link String}。
     */
    @Genericable("modelengine.jade.document.service.extract")
    String invoke(FileExtractionParam param);
}
