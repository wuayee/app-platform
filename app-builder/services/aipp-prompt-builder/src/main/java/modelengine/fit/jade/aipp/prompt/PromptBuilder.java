/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

import java.util.Map;
import java.util.Optional;

/**
 * 提示器构造器。
 *
 * @author 刘信宏
 * @since 2024-12-02
 */
public interface PromptBuilder {
    /**
     * 构造提示词。
     *
     * @param userAdvice 表示用户提示词建议的 {@link UserAdvice}。
     * @param context 表示上下文数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示提示词消息的 {@link Optional}{@code <}{@link PromptMessage}{@code >}。
     */
    Optional<PromptMessage> build(UserAdvice userAdvice, Map<String, Object> context);

    /**
     * 获取构造策略。
     *
     * @return 表示提示词构造策略的 {@link PromptStrategy}。
     */
    PromptStrategy strategy();
}
