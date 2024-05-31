/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.rerank;

import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.rag.Chunk;
import com.huawei.jade.fel.rag.Chunks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 利用大模型进行重排序。
 *
 * @since 2024-05-07
 */
public class LlmRerank extends ModelRerank {
    private static final Pattern INDICES = Pattern.compile("\\d+");
    private static final Logger logger = Logger.get(LlmRerank.class);

    private static final String PROMPT = "The following are {{num}} passages, each indicated by number identifier []."
            + " Please rank them based on thier relevance to query: {{query}}\\n{{passages}}\\n"
            + "Output MUST contain the {{num}} identifiers listed above, Do not give any additional message\\n"
            + "For example, \\\"[1, 2, 3]\\\" is a well-formed output and 1. [1] \\n 2.[2] \\n 3.[3] is a bad output";

    private AiProcessFlow<Tip, ChatMessage> rerankFlow;

    /**
     * 利用传入的重排序模型创建 {@link LlmRerank} 实例。
     *
     * @param modelService 表示重排序模型的 {@link ChatModelService}。
     */
    public LlmRerank(ChatModelService modelService) {
        rerankFlow = AiFlows.<Tip>create()
                .prompt(Prompts.human(PROMPT)).generate(new ChatBlockModel<>(modelService)).close();
    }

    /**
     * 通过大模型将检索到的数据与查询信息的相关性进行重排序。
     *
     * @param query 表示来自检索器的查询字段的 {@link String}。
     * @param data 表示经过索引检索到的数据的 {@link List}{@code <}{@link Chunk}{@code >}。
     * @return 返回排好序的数据。
     */
    @Override
    public Chunks invoke(String query, Chunks data) {
        List<Chunk> chunks = data.getChunks();

        String generated = rerankFlow.converse().offer(buildTip(query, chunks)).await().text();

        List<Integer> indices = getIndices(generated);
        if (indices.isEmpty()) {
            return data;
        }

        try {
            chunks = indices.stream()
                    .map(chunks::get)
                    .collect(Collectors.toList());
        } catch (IndexOutOfBoundsException e) {
            logger.error("Indices: " + indices + "\n" + e.getMessage());
        }

        return Chunks.from(chunks);
    }

    private Tip buildTip(String query, List<Chunk> chunks) {
        String passages = "";
        for (int i = 0; i < chunks.size(); i++) {
            passages += "[" + (i + 1) + "] " + chunks.get(i).getContent() + "\\n";
        }

        return Tip.from("num", String.valueOf(chunks.size()))
                .add("query", query)
                .add("passages", passages);
    }

    private static List<Integer> getIndices(String input) {
        List<Integer> result = new ArrayList<>();

        try {
            Matcher matcher = INDICES.matcher(input);

            while (matcher.find()) {
                result.add(Integer.parseInt(matcher.group()) - 1);
            }
        } catch (NumberFormatException e) {
            logger.warn("output from llm is not well formatted");
        }

        return result;
    }
}
