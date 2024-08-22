/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.plugins.tokensplitter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fel.rag.Document;
import modelengine.fel.rag.protocol.FlatChunk;
import modelengine.fel.rag.protocol.FlatDocument;
import modelengine.fel.rag.split.SplitOptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Token切分服务测试类。
 *
 * @since 2024-06-04
 */
public class TokenSplitterServiceTest {
    private List<FlatDocument> flatDocuments;

    @BeforeEach
    void init() {
        flatDocuments = Arrays.asList(
                new FlatDocument(new Document("1", "content1" + "content2" + "content3", null))
        );
    }

    @Test
    void test_token_splitter() {
        TokenSplitterService service = new TokenSplitterService();
        List<FlatChunk> chunks = service.split(flatDocuments, SplitOptions.builder().tokenSize(8).overlap(1).build());

        assertEquals(4, chunks.size());
        assertEquals("content1", chunks.get(0).getContent());
        assertEquals("1content", chunks.get(1).getContent());
        assertEquals("t2conten", chunks.get(2).getContent());
        assertEquals("nt3", chunks.get(3).getContent());
    }
}
