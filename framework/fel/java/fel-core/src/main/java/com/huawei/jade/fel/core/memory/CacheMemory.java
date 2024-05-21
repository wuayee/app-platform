/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.memory;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.core.template.BulkStringTemplate;
import com.huawei.jade.fel.core.template.support.DefaultBulkStringTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link Memory} 的简单内存实现。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class CacheMemory implements Memory {
    private final List<ChatMessage> messages = new ArrayList<>();
    private final BulkStringTemplate bulkTemplate;
    private final Function<ChatMessage, Map<String, String>> processor;

    /**
     * 设置内存中构建器的默认模板和关键词。
     *
     * @see <a href="https://mustache.github.io/">mustache</a>。
     */
    public CacheMemory() {
        this("{{type}}:{{text}}", "\n",
                cm -> MapBuilder.<String, String>get()
                        .put("type", cm.type().toString().toLowerCase(Locale.ROOT))
                        .put("text", cm.text())
                        .build());
    }

    /**
     * 设置内存中构建器的模板和关键词。
     *
     * @param template 表示字符串模板的 {@link String}。
     * @param delimiter 表示分隔符。
     * @param processor 表示将 {@link ChatMessage} 转换成
     * {@link Map}{@code <}{@link String}, {@link String}{@code >} 的处理函数。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public CacheMemory(String template, String delimiter,
            Function<ChatMessage, Map<String, String>> processor) {
        Validation.notBlank(template, "The template cannot be blank.");
        Validation.notNull(delimiter, "The delimiter cannot be null.");
        this.bulkTemplate = new DefaultBulkStringTemplate(template, delimiter);
        this.processor = Validation.notNull(processor, "The processor cannot be null.");
    }

    /**
     * 设置内存中构建器的模板和关键词。
     *
     * @param bulkTemplate 表示批量字符串模板 {@link BulkStringTemplate}。
     * @param processor 表示将 {@link ChatMessage} 转换成
     * {@link Map}{@code <}{@link String}, {@link String}{@code >} 的处理函数。
     */
    public CacheMemory(BulkStringTemplate bulkTemplate, Function<ChatMessage, Map<String, String>> processor) {
        this.bulkTemplate = Validation.notNull(bulkTemplate, "The bulkTemplate cannot be null.");
        this.processor = Validation.notNull(processor, "The processor cannot be null.");
    }

    @Override
    public void add(ChatMessage question, ChatMessage answer) {
        this.messages.add(Validation.notNull(question, "Question cannot be null."));
        this.messages.add(Validation.notNull(answer, "Answer cannot be null."));
    }

    @Override
    public List<ChatMessage> messages() {
        return Collections.unmodifiableList(this.messages);
    }

    @Override
    public String text() {
        return this.messages.stream()
                .map(this.processor)
                .collect(Collectors.collectingAndThen(Collectors.toList(), this.bulkTemplate::render));
    }
}