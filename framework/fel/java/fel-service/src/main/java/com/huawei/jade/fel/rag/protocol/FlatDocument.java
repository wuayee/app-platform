/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.protocol;

import com.huawei.jade.fel.rag.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用于Fitable调用的文档适配类。
 *
 * @since 2024-06-03
 */

@Getter
@AllArgsConstructor
public class FlatDocument {
    private final String id;
    @Setter
    private String content;
    @Setter
    private List<List<String>> table;

    /**
     * 利用Chunk构造 {@link FlatDocument} 实例。
     *
     * @param document 表示文档的{@link Document}。
     */
    public FlatDocument(Document document) {
        this(document.getId(), document.getContent(), document.getTable());
    }

    /**
     * 将自身转换为{@link Document}。
     *
     * @return 返回转换后的文档
     */
    public Document toDocument() {
        Document doc = new Document(id, content, null);
        doc.setTable(table);
        return doc;
    }
}
