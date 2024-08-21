/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示一个经过处理后的数据切片，是RAG流内部使用的最小数据类型。
 * <p>经由Split处理后的数据都封装在Chunk中。</p>
 *
 * @since 2024-05-07
 */
@Getter
public class Chunk {
    private String id;
    @Setter
    private String content;

    @Setter
    private List<String> rowContent;

    /**
     * 待业务逻辑清晰后进行抽象
     */
    @Setter
    private Map<String, Object> metadata;

    /**
     * 使用唯一标识、内容和元信息创建 {@link Chunk} 的实例。
     *
     * @param id 表示唯一标识的 {@link String}。
     * @param content 表示内容的 {@link String}。
     * @param metadata 表示元信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Chunk(@Nonnull String id, @Nonnull String content, Map<String, Object> metadata) {
        this.id = id;
        this.content = content;
        this.metadata = ObjectUtils.getIfNull(metadata, HashMap::new);
    }

    /**
     * 使用唯一标识、内容、元信息和父级 {@link Document} 唯一标识创建 {@link Chunk} 的实例。
     *
     * @param id 表示唯一标识的 {@link String}。
     * @param content 表示内容的 {@link String}。
     * @param metadata 表示元信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param sourceId 表示父级 {@link Document} 唯一标识的 {@link String}
     */
    public Chunk(@Nonnull String id, @Nonnull String content, Map<String, Object> metadata, @Nonnull String sourceId) {
        this(id, content, metadata);
        this.metadata.put("sourceId", sourceId);
    }

    public Chunk(@Nonnull String id, @Nonnull List<String> rowContent, Map<String, Object> metadata) {
        this.id = id;
        this.rowContent = rowContent;
        this.metadata = ObjectUtils.getIfNull(metadata, HashMap::new);
    }

    /**
     * 添加Chunk的元信息。
     *
     * @param key 表示元信息key的 {@link String}。
     * @param value 表示元信息的value的 {@link Object}。
     * @return 返回 {@link Chunk} 自身。
     */
    public Chunk addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Chunk chunk = ObjectUtils.as(obj, Chunk.class);
        return Objects.equals(id, chunk.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
