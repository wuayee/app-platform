/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt.constant;

import modelengine.fitframework.annotation.Order;

/**
 * {@link ProcessBuilder} 的优先级定义。
 *
 * @author 刘信宏
 * @since 2024-12-09
 */
public interface PromptBuilderOrder {
    /**
     * 默认提示词构造器的优先级。
     */
    int DEFAULT = Order.LOWEST;

    /**
     * 溯源提示词构造器的优先级。
     */
    int REFERENCE = Order.LOW;
}
