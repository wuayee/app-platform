/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.langchain.retriever;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.character.HumanMessage;
import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.langchain.model.LangChainBlockModel;
import com.huawei.jade.fel.service.langchain.LangChainRunnableService;
import com.huawei.jade.fel.tool.ToolCall;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * {@link LangChainBlockModel} 的测试。
 *
 * @author l00611472
 * @since 2024-06-13
 */
public class LangChainModelTest {
    private LangChainRunnableService runnableServiceStub;

    @BeforeEach
    void setUp() {
        this.runnableServiceStub = (taskId, fitableId, input) -> {
            Map<String, Object> mediaMap = MapBuilder.<String, Object>get()
                    .put("data", "data")
                    .put("mimeType", "image/png")
                    .build();
            Map<String, Object> toolCallsMap = MapBuilder.<String, Object>get()
                    .put("id", "toolId")
                    .put("name", "toolName")
                    .put("parameters", "toolParameters")
                    .build();

            return MapBuilder.<String, Object>get()
                    .put("type", "ai")
                    .put("text", "model answer")
                    .put("medias", Collections.singletonList(mediaMap))
                    .put("toolCalls", Collections.singletonList(toolCallsMap))
                    .build();
        };
    }

    @Test
    void shouldOkWhenFlowOfferSource() {
        LangChainBlockModel model = new LangChainBlockModel(runnableServiceStub, "model");
        ChatMessage query = model.invoke(ChatMessages.from(new HumanMessage("query")));

        assertThat(query.type()).isEqualTo(MessageType.AI);
        assertThat(query.text()).isEqualTo("model answer");
        assertThat(query.medias()).hasSize(1).containsSequence(new Media("data", "image/png"));
        assertThat(query.toolCalls()).hasSize(1)
                .containsSequence(new ToolCall("toolId", "toolName", "toolParameters"));
    }
}
