/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.langchain.retriever;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.fel.retrieve.Document;
import com.huawei.jade.fel.service.langchain.LangChainRunnableService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link LangChainRetriever} 的测试。
 *
 * @author 刘信宏
 * @since 2024-06-13
 */
public class LangChainRetrieverTest {
    private LangChainRunnableService runnableServiceStub;

    @BeforeEach
    void setUp() {
        this.runnableServiceStub = (taskId, fitableId, input) -> {
            Map<String, Object> expectedDocs = MapBuilder.<String, Object>get()
                    .put("content", "retriever_content")
                    .put("metadata", Collections.singletonMap("source", "local_doc"))
                    .build();
            return Collections.singletonList(expectedDocs);
        };
    }

    @Test
    void shouldOkWhenFlowOfferSource() {
        LangChainRetriever langChainRetriever = new LangChainRetriever(this.runnableServiceStub, "test_retriever");

        List<Document> query = langChainRetriever.invoke("query");
        assertThat(query).hasSize(1);
        assertThat(query.get(0).text()).isEqualTo("retriever_content");
        assertThat(query.get(0).meta()).containsKey("source");
    }
}
