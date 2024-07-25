/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.split;

import com.huawei.jade.fel.core.retriever.Splitter;
import com.huawei.jade.fel.rag.Chunk;
import com.huawei.jade.fel.rag.Document;
import com.huawei.jade.fel.rag.common.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 按字符数目进行切分。
 *
 * @since 2024-05-17
 */
public class TokenSplitter implements Splitter<List<Document>, List<Chunk>> {
    private final int tokenSize;
    private final int overlap;

    /**
     * 根据传入参数构造{@link TokenSplitter}的实例
     *
     * @param tokenSize 表示单一分段的最大长度 {@link int}。
     * @param overlap 表示分段之间重叠部分的最大长度 {@link int}
     */
    public TokenSplitter(int tokenSize, int overlap) {
        this.tokenSize = tokenSize;
        this.overlap = overlap;
    }

    @Override
    public List<Chunk> split(List<Document> input) {
        return input.stream()
                .flatMap(doc -> split(doc).stream())
                .collect(Collectors.toList());
    }

    private List<Chunk> split(Document input) {
        List<Chunk> ret = new ArrayList<>();
        String content = input.getContent();

        int step = tokenSize - overlap;
        for (int i = 0; i < content.length(); i += step) {
            int endIdx = Math.min(i + tokenSize, content.length());
            String chunkContent = content.substring(i, endIdx);
            ret.add(new Chunk(IdGenerator.getId(), chunkContent, null, input.getId()));
        }
        return ret;
    }
}
