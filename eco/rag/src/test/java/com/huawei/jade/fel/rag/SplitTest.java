/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.rag.split.CharacterTextSplitter;

import com.huawei.jade.fel.rag.split.TokenSplitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Split模块测试。
 *
 * @since 2024-05-08
 */
public class SplitTest {
    List<Document> docs;
    List<Document> docs2;

    @BeforeEach
    void init() {
        docs = Arrays.asList(
                new Document("1",
                        "content1" + System.lineSeparator() + "content2" + System.lineSeparator() + "content3",
                        null)
        );
        docs2 = Arrays.asList(
                new Document("1", "content1" + "content2" + "content3", null)
        );
    }

    @Test
    void test_sentence_splitter() {
        CharacterTextSplitter splitter = new CharacterTextSplitter(System.lineSeparator());

        List<Chunk> chunks = splitter.split(docs);
        assertEquals("content1", chunks.get(0).getContent());
        assertEquals("content2", chunks.get(1).getContent());
        assertEquals("content3", chunks.get(2).getContent());
    }

    @Test
    void test_token_splitter() {
        TokenSplitter splitter = new TokenSplitter(8, 1);
        List<Chunk> chunks = splitter.split(docs2);

        assertEquals(4, chunks.size());
        assertEquals("content1", chunks.get(0).getContent());
        assertEquals("1content", chunks.get(1).getContent());
        assertEquals("t2conten", chunks.get(2).getContent());
        assertEquals("nt3", chunks.get(3).getContent());
    }
}
