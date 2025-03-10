/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory;

import lombok.Data;

/**
 * 表示历史记录相关配置的接口。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
@Data
public class AippMemoryConfig {
    /**
     * 历史记录消费策略：
     * <ul>
     *     <li>buffer_window：按对话轮次；</li>
     *     <li>token_window：按 token 数目。</li>
     * </ul>
     */
    private String windowAlg;

    /**
     * 历史记录序列化策略：
     * <ul>
     *     <li>full：使用问题和答案；</li>
     *     <li>question_only：只使用问题。</li>
     * </ul>
     */
    private String serializeAlg;

    /**
     * memory 的属性，根据 {@code windowAlg} 不同而变化，有以下含义：
     * <ul>
     *     <li>表示最大对话轮次；</li>
     *     <li>表示最大 token 数。</li>
     * </ul>
     */
    private Object property;
}