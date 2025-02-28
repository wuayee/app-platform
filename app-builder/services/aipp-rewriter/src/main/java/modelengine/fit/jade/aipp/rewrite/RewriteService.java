/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite;

import modelengine.fit.jade.aipp.memory.AippChatRound;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 重写算子服务。
 *
 * @author 易文渊
 * @since 2024-09-28
 */
public interface RewriteService {
    /**
     * 重写问题。
     *
     * @param rewriteParam 表示重写参数的 {@link RewriteQueryParam}。
     * @param memoryConfig 表示历史理解配置的 {@link AippMemoryConfig}。
     * @param histories 表示历史记录的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     * @return 表示重写后问题列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable("modelengine.jober.aipp.rewrite.query")
    List<String> rewriteQuery(RewriteQueryParam rewriteParam, AippMemoryConfig memoryConfig,
            List<AippChatRound> histories);
}