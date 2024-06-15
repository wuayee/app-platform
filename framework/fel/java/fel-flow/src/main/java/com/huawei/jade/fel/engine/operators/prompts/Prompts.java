/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.prompts;

import com.huawei.jade.fel.core.template.StringTemplate;
import com.huawei.jade.fel.core.template.support.DefaultStringTemplate;

/**
 * 提示词工具方法。用于 {@link com.huawei.jade.fel.engine.activities.AiStart#prompt(PromptTemplate[])} 构造提示词。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public class Prompts {
    /**
     * 构造人类提示词模板。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @return 表示人类提示词模板的 {@link HumanTemplate}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public static HumanTemplate human(String template) {
        return human(new DefaultStringTemplate(template));
    }

    /**
     * 构造人类提示词模板。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     * @return 表示人类提示词模板的 {@link HumanTemplate}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public static HumanTemplate human(StringTemplate template) {
        return new HumanTemplate(template);
    }

    /**
     * 构造系统提示词模板。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @return 表示系统提示词模板的 {@link SystemTemplate}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public static SystemTemplate sys(String template) {
        return sys(new DefaultStringTemplate(template));
    }

    /**
     * 构造系统提示词模板。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     * @return 表示系统提示词模板的 {@link SystemTemplate}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public static SystemTemplate sys(StringTemplate template) {
        return new SystemTemplate(template);
    }

    /**
     * 构造历史记录提示词模板。
     *
     * @param <I> 表示提示词模板入参的类型。
     * @return 表示提示词模板的 {@link PromptTemplate}{@code <}{@link I}{@code >}。
     */
    public static <I> PromptTemplate<I> history() {
        return new HistoryTemplate<>();
    }
}
