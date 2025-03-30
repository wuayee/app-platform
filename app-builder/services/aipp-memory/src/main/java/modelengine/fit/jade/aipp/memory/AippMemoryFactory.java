/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory;

import modelengine.fel.core.memory.Memory;

import java.util.List;

/**
 * 表示历史记录工厂的接口。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
public interface AippMemoryFactory {
    /**
     * 根据配置构建历史记录。
     *
     * @param config 表示历史记录配置的 {@link AippMemoryConfig}。
     * @param histories 表示历史记录的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     * @return 表示历史记录的 {@link Memory}。
     */
    Memory create(AippMemoryConfig config, List<AippChatRound> histories);
}