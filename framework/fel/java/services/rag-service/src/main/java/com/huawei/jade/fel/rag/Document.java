/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag;

import modelengine.fitframework.util.ObjectUtils;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对RAG流处理的数据进行封装。
 * <p>任何类型的数据在刚进入RAG流时都会转换为{@link Document}.</p>
 *
 * @since 2024-05-07
 */
@Getter
public class Document {
    private final String id;
    @Setter
    private String content;
    @Setter
    private Map<String, Object> metadata;
    @Setter
    private List<List<String>> table;

    /**
     *
     * 使用唯一标识、内容和元信息创建 {@link Document}的实例。
     *
     * @param id 表示唯一标识的 {@link String}。
     * @param content 表示内容的 {@link String}。
     * @param metadata 表示元信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Document(String id, String content, Map<String, Object> metadata) {
        this.id = id;
        this.content = content;
        this.metadata = ObjectUtils.nullIf(metadata, new HashMap<>());
    }

    public Document(String id, List<List<String>> tableContent, Map<String, Object> metadata) {
        this.id = id;
        this.table = tableContent;
        this.metadata = ObjectUtils.nullIf(metadata, new HashMap<>());
    }
}
