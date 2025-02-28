/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory;

import modelengine.fel.core.memory.Memory;
import modelengine.fel.core.template.BulkStringTemplate;

import java.util.List;

/**
 * aipp memory 初始化接口定义。
 *
 * @author 易文渊
 * @since 2024-09-25
 */
@FunctionalInterface
public interface AippMemoryInitializer {
    /**
     * 根据参数初始化 memory。
     *
     * @param histories 表示历史记录列表的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     * @param property 表示配置参数的 {@code property}。
     * @param template 表示模板的 {@link BulkStringTemplate}。
     * @return 表示历史记忆对象的 {@link Memory}。
     */
    Memory create(List<AippChatRound> histories, Object property, BulkStringTemplate template);
}