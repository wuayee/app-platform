/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.constant;

import modelengine.fit.jade.aipp.formatter.OutputFormatter;
import modelengine.fitframework.annotation.Order;

/**
 * {@link OutputFormatter} 的优先级定义。
 *
 * @author 刘信宏
 * @since 2024-11-21
 */
public interface OutputFormatterOrder {
    /**
     * 默认格式化器的优先级。
     */
    int DEFAULT = Order.LOWEST;

    /**
     * 大模型节点输出报文格式化器的优先级。
     */
    int LLM_OUTPUT = Order.LOW;
}
