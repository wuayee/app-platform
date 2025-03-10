/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.domain.entity;

import modelengine.fel.core.chat.ChatOption;

import java.util.Map;

/**
 * 表示信息提取算子。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-24
 */
public interface ContentExtractor {
    /**
     * 处理信息提取。
     *
     * @param variables 表示输入参数的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @param outputSchema 表示输出 json 规范的 {@link String}。
     * @param chatOption 表示推理参数的 {@link ChatOption}。
     * @return 表示提取结果的 {@link Object}。
     */
    Object run(Map<String, String> variables, String outputSchema, ChatOption chatOption);
}