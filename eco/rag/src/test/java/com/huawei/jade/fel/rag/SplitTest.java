/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.huawei.jade.fel.rag.common.IdGenerator;
import com.huawei.jade.fel.rag.split.CharacterTextSplitter;
import com.huawei.jade.fel.rag.split.SentenceSplitter;
import com.huawei.jade.fel.rag.split.TokenSplitter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                        "content1" + System.lineSeparator() + System.lineSeparator() + "content2"
                                + System.lineSeparator() + "content3", null)
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

    @Test
    void test_mixed_sentence_split() {
        String text = "This is a mixed text. This is a mixed text?This is a 3.1415926 sentence!这是一个“混合文本”混合文本。"
                + "这是混合“文本?混合文本.混合文本”。It includes both English sentences and 中文句子。"
                + " And quotes like \"This is a sentence in quotes.This is a sentence in quotes?"
                + "This is a 3.1415926 sentence in quotes.\"This is a mixed text. "
                + "This is a mixed text?This is a mixed text!这是一个“混合文本”忽略引号“混合文本”。这是一个测试。请注意，"
                + "这个例子中有各种...标点符号！包括省略号。。。要保留完整的引号内容“这是引号中的句子”。这是一个测试。“保留完整的引号内容。”";
        SentenceSplitter splitter = new SentenceSplitter();
        List<String> split = splitter
                .split(Arrays.asList(new Document(IdGenerator.getId(), text, null))).stream()
                .map(Chunk::getContent)
                .collect(Collectors.toList());
        assertTrue(split.contains("This is a mixed text."));
        assertTrue(split.contains("This is a 3.1415926 sentence!"));
        assertTrue(split.contains("这是一个“混合文本”混合文本。"));
        assertTrue(split.contains("这是混合“文本?混合文本.混合文本”。"));
        assertTrue(split.contains("请注意，这个例子中有各种...标点符号！"));
        assertTrue(split.contains("包括省略号。。。要保留完整的引号内容“这是引号中的句子”。"));
        assertTrue(split.contains("“保留完整的引号内容。”"));
    }
}
