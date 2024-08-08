/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.vectorstore.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.fel.core.document.MeasurableDocument;
import com.huawei.jade.fel.core.embed.EmbedOption;
import com.huawei.jade.fel.core.embed.support.DefaultDocumentEmbedModel;
import com.huawei.jade.fel.core.vectorstore.SearchOption;
import com.huawei.jade.fel.core.vectorstore.VectorStore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

/**
 * 表示 {@link MemoryVectorStore} 的单元测试。
 *
 * @author 易文渊
 * @since 2024-08-08
 */
@DisplayName("测试 MemoryVectorStore")
public class MemoryVectorStoreTest {
    @Test
    @DisplayName("插入文档后，查询成功")
    void shouldOkWhenAddDocumentsThenSearch() {
        VectorStore vectorStore = new MemoryVectorStore(new DefaultDocumentEmbedModel(new EmbedModelStub(),
                EmbedOption.custom().build()));
        vectorStore.persistent(EmbedModelStub.generateTestDocuments());
        assertThat(vectorStore.search("test", SearchOption.custom().topK(3).build())).hasSize(3)
                .isSortedAccordingTo(Comparator.comparingDouble(MeasurableDocument::score).reversed());
    }

    @Test
    @DisplayName("插入文档后，持久化后重新加载成功")
    void shouldOkWhenPersistThenLoad() throws IOException {
        ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

        MemoryVectorStore vectorStore = new MemoryVectorStore(new DefaultDocumentEmbedModel(new EmbedModelStub(),
                EmbedOption.custom().build()));
        vectorStore.persistent(EmbedModelStub.generateTestDocuments());
        List<MeasurableDocument> first = vectorStore.search("test", SearchOption.custom().topK(3).build());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(1024)) {
            vectorStore.persist(out, serializer);
            byte[] bytes = out.toByteArray();
            try (InputStream in = new ByteArrayInputStream(bytes)) {
                MemoryVectorStore resumeStore =
                        new MemoryVectorStore(new DefaultDocumentEmbedModel(new EmbedModelStub(),
                                EmbedOption.custom().build()));
                resumeStore.load(in, serializer);
                List<MeasurableDocument> second = resumeStore.search("test", SearchOption.custom().topK(3).build());
                assertThat(first).isEqualTo(second);
            }
        }
    }
}