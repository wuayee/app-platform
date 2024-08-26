/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.split;

import modelengine.fel.core.retriever.Splitter;
import modelengine.fel.rag.Chunk;
import modelengine.fel.rag.Document;
import modelengine.fel.rag.common.IdGenerator;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据标点符号切分句子
 * <p>支持混合中英文切分，且双引号内部不切分</p>
 *
 * @since 2024-06-28
 */
public class SentenceSplitter implements Splitter<List<Document>, List<Chunk>> {
    private final List<String> sentenceEnding = Arrays.asList(".", "?", "!", "。", "？", "！");

    @Override
    public List<Chunk> split(List<Document> input) {
        return input.stream()
                .flatMap(doc -> split(doc).stream())
                .collect(Collectors.toList());
    }

    private List<Chunk> split(Document doc) {
        List<String> sentences = split(doc.getContent());

        return sentences.stream()
                .map(sentence -> new Chunk(IdGenerator.getId(), sentence, new HashMap<>(), doc.getId()))
                .collect(Collectors.toList());
    }

    private List<String> split(String text) {
        boolean isInQuote = false;
        StringBuilder sb = new StringBuilder();
        List<String> sentences = new ArrayList<>();
        Segment segment = HanLP.newSegment().enableIndexMode(false);
        List<Term> termList = segment.seg(text);

        for (Term term : termList) {
            String word = term.word;
            sb.append(word);

            if (isQuote(word)) {
                isInQuote = !isInQuote;
            }

            if (!isInQuote && isSentenceEnding(term)) {
                sentences.add(sb.toString());
                sb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            sentences.add(sb.toString());
        }

        return sentences;
    }

    private boolean isQuote(String word) {
        return "\"".equals(word) || (word.length() == 2 && word.endsWith("\""))
                || "“".equals(word) || "”".equals(word);
    }

    private boolean isSentenceEnding(Term term) {
        return sentenceEnding.contains(term.word);
    }
}
