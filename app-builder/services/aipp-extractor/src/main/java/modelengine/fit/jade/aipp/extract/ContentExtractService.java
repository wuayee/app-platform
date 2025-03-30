/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract;

import modelengine.fit.jade.aipp.memory.AippChatRound;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 表示上下文提取服务。
 *
 * @author 易文渊
 * @since 2024-10-24
 */
public interface ContentExtractService {
    /**
     * 提取信息。
     *
     * @param extractParam 表示提取参数的 {@link ContentExtractParam}。
     * @param memoryConfig 表示历史理解配置的 {@link AippMemoryConfig}。
     * @param histories 表示历史记录的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     * @return 表示提取出信息的 {@link Object}。
     */
    @Genericable("modelengine.jober.aipp.extract")
    ExtractResult extract(ContentExtractParam extractParam, AippMemoryConfig memoryConfig,
            List<AippChatRound> histories);
}