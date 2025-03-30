/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.vo;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.chat.ChatOption;

import java.util.Map;

/**
 * 表示重写参数的值对象。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public class RewriteParam {
    private final String template;
    private final Map<String, String> variables;
    private final ChatOption chatOption;

    /**
     * 创建 {@link RewriteParam} 的实例。
     *
     * @param template 表示模板的 {@link String}。
     * @param variables 表示输入参数的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @param chatOption 表示推理参数的 {@link ChatOption}。
     * @throws IllegalArgumentException 如果模板、输入参数或推理参数为 {@code null}。
     */
    public RewriteParam(String template, Map<String, String> variables, ChatOption chatOption) {
        this.template = notNull(template, "The template cannot be null.");
        this.variables = notNull(variables, "The variables cannot be null.");
        this.chatOption = notNull(chatOption, "The chat option cannot be null.");
    }

    /**
     * 获取模板。
     *
     * @return 表示模板的 {@link String}。
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * 获取输入参数。
     *
     * @return 表示输入参数的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public Map<String, String> getVariables() {
        return this.variables;
    }

    /**
     * 获取推理参数。
     *
     * @return 表示推理参数的 {@link ChatOption}。
     */
    public ChatOption getChatOption() {
        return this.chatOption;
    }
}