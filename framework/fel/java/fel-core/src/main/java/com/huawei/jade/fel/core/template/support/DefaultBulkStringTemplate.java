/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.template.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.template.BulkStringTemplate;
import com.huawei.jade.fel.core.template.StringTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示批量生成字符串模板的实现。
 *
 * @author 何嘉斌
 * @since 2024-05-13
 */
public class DefaultBulkStringTemplate implements BulkStringTemplate {
    private final String delimiter;
    private final StringTemplate template;

    /**
     * 使用 mustache 模板语法创建 {@link DefaultBulkStringTemplate} 的实例。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @param delimiter 表示分隔符。
     * @throws IllegalArgumentException 当模板和输入占位符不匹配时。
     * @see <a href="https://mustache.github.io/">mustache</a>。
     */
    public DefaultBulkStringTemplate(String template, String delimiter) {
        this(new DefaultStringTemplate(template), delimiter);
    }

    /**
     * 使用 mustache 模板语法创建 {@link DefaultBulkStringTemplate} 的实例。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     * @param delimiter 表示分隔符。
     * @throws IllegalArgumentException 当模板和输入占位符不匹配时。
     */
    public DefaultBulkStringTemplate(StringTemplate template, String delimiter) {
        this.delimiter = Validation.notNull(delimiter, "The delimiter cannot be null.");
        this.template = Validation.notNull(template, "The template cannot be null.");
    }

    /**
     * 将输入的映射数组的内容按照模板渲染。
     *
     * @param values 表示输入的映射数组。
     * {@link List}{@code <}{@link Map}{@code <}{@link String}, {@link String}{@code >}{@code >}。
     * @return 表示构建完成的字符串。
     */
    @Override
    public String render(List<Map<String, String>> values) {
        return values.stream().map(this.template::render).collect(Collectors.joining(this.delimiter));
    }

    @Override
    public Set<String> placeholder() {
        return this.template.placeholder();
    }
}
