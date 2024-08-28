/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.plugins.llmrerank;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fel.chat.ChatModelService;
import modelengine.fel.rag.Chunks;
import modelengine.fel.rag.protocol.FlatChunk;
import modelengine.fel.rag.rerank.LlmRerank;
import modelengine.fel.rag.rerank.ModelRerankService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 利用模型进行重排序的大模型实现。
 *
 * @since 2024-06-03
 */
@Component
public class LlmRerankService implements ModelRerankService {
    private final ChatModelService chatModelService;

    /**
     * 根据传入的对话模型服务构造{@link LlmRerankService}的实例。
     *
     * @param chatModelService 表示聊天模型服务的{@link ChatModelService}。
     */
    public LlmRerankService(ChatModelService chatModelService) {
        this.chatModelService = Validation.notNull(chatModelService, "Chat model service cannot be null");
    }

    @Override
    @Fitable("llm-rerank")
    public List<FlatChunk> rerank(String query, List<FlatChunk> flatChunks) {
        return new LlmRerank(this.chatModelService)
                .invoke(query, Chunks.from(flatChunks.stream().map(FlatChunk::toChunk).collect(Collectors.toList())))
                .getChunks().stream()
                .map(FlatChunk::new)
                .collect(Collectors.toList());
    }
}
