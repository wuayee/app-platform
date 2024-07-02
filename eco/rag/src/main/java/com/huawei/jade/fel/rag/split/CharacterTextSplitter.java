/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.split;

import com.huawei.jade.fel.core.retriever.Splitter;
import com.huawei.jade.fel.rag.Chunk;
import com.huawei.jade.fel.rag.Document;
import com.huawei.jade.fel.rag.common.IdGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 按字符进行切分。
 *
 * @since 2024-05-08
 */
public class CharacterTextSplitter implements Splitter<List<Document>, List<Chunk>> {
    private String separator;

    /**
     * 根据传入的分割符构造 {@link CharacterTextSplitter} 的实例。
     *
     * @param separator 表示分隔符的 {@link String}。
     */
    public CharacterTextSplitter(String separator) {
        this.separator = separator;
    }

    @Override
    public List<Chunk> split(List<Document> input) {
        return input.stream()
                .flatMap(doc -> split(doc).stream())
                .collect(Collectors.toList());
    }

    private List<Chunk> split(Document doc) {
        String[] sentences = doc.getContent().split(separator);

        return Arrays.stream(sentences)
                .filter(sentence -> !sentence.replace("\u3000", " ").trim().isEmpty())
                .map(sentence -> new Chunk(IdGenerator.getId(), sentence, new HashMap<>(), doc.getId()))
                .collect(Collectors.toList());
    }
}
