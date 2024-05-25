/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool;

import com.huawei.fitframework.inspection.Validation;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义工具上下文。
 *
 * @author 刘信宏
 * @since 2024-05-24
 */
public class ToolContext {
    private final Map<String, String> context = new HashMap<>();

    /**
     * 从键值对创建 {@link ToolContext} 的实例。
     *
     * @param key 表示占位符的 {@link String}。
     * @param value 表示替换值的 {@link String}。
     * @return 返回创建完成的 {@link ToolContext}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    public static ToolContext from(String key, String value) {
        return new ToolContext().add(key, value);
    }

    /**
     * 添加字符串值。
     *
     * @param key 表示占位符的 {@link String}。
     * @param value 表示替换值的 {@link String}。
     * @return 表示当前的 {@link ToolContext}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    public ToolContext add(String key, String value) {
        Validation.notNull(key, "The key cannot be null");
        this.context.put(key, value);
        return this;
    }

    /**
     * 批量添加字符串值。
     *
     * @param context 表示键值对的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @return 表示当前的 {@link ToolContext}。
     * @throws IllegalArgumentException 当 {@code context} 中存在健为 {@code null} 时。
     */
    public ToolContext addAll(Map<String, String> context) {
        context.forEach(this::add);
        return this;
    }

    /**
     * 获取字符串值。
     *
     * @param key 表示占位符的 {@link String}。
     * @return 表示对应值的 {@link String}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    public String match(String key) {
        Validation.notNull(key, "The key cannot be null");
        return this.context.get(key);
    }
}
