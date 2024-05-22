/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.split;

import com.huawei.jade.fel.core.retriever.Splitter;
import com.huawei.jade.fel.rag.common.Chunk;
import com.huawei.jade.fel.rag.common.Document;
import com.huawei.jade.fel.rag.common.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * 将粗处理的表格内容按行切分存入chunk。
 *
 * @since 2024-05-22
 */
public class TableSplitter implements Splitter<List<Document>, List<Chunk>> {
    @Override
    public List<Chunk> split(List<Document> input) {
        List<Chunk> out = new ArrayList<>();
        input.forEach(document -> {
            document.getTable().forEach(rowContent -> {
                out.add(new Chunk(IdGenerator.getId(), rowContent, null));
            });
        });
        return out;
    }
}
