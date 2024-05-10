/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.common;

import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.chat.content.MessageContent;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Chunk列表的封装。
 * <p>实现 {@link MessageContent} 接口。</p>
 *
 * @since 2024-05-07
 */
public class Chunks implements MessageContent {
    @Getter
    private List<Chunk> chunks;

    /**
     * 构造 {@link Chunks} 的实例。
     */
    public Chunks() {
        this.chunks = new ArrayList<>();
    }

    /**
     * 从 {@link Chunk} 列表构造 {@link Chunks}。
     *
     * @param data 表示Chunk列表的 {@link List}{@code <}{@link Chunk}{@code >}。
     * @return 返回 {@link Chunks} 实例。
     */
    public static Chunks from(List<Chunk> data) {
        Chunks res = new Chunks();
        res.chunks = data;
        return res;
    }

    /**
     * Chunks的迭代器。
     *
     * @return 返回 {@link List}{@code <}{@link Chunk}{@code >}的迭代器。
     */
    public Iterator<Chunk> iterator() {
        return this.chunks.iterator();
    }

    @Override
    public String text() {
        return chunks.stream()
                .map(chunk -> chunk.getContent())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public List<Media> medias() {
        return Collections.emptyList();
    }
}
