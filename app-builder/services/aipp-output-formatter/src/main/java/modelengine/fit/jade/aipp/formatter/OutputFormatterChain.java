/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import modelengine.fit.jade.aipp.formatter.support.ResponsibilityResult;

import java.util.List;
import java.util.Optional;

/**
 * {@link OutputFormatter} 的职责链。
 *
 * @author 刘信宏
 * @since 2024-11-21
 */
public interface OutputFormatterChain {
    /**
     * 获取格式化器职责链。
     *
     * @return 表示格式化器职责链的 {@link List}{@code <}{@link OutputFormatter}{@code >}。
     */
    List<OutputFormatter> get();

    /**
     * 执行职责链，使用首个匹配的格式化器进行格式化。
     *
     * @param data 表示应用响应数据的 {@link Object}。
     * @return 表示职责链的响应结果的 {@link Optional}{@code <}{@link ResponsibilityResult}{@code >}。
     */
    Optional<ResponsibilityResult> handle(Object data);
}
