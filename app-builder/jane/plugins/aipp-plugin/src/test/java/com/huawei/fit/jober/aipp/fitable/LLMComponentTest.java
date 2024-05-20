/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static com.huawei.fit.jober.aipp.TestUtils.mockResumeFlow;
import static com.huawei.fit.jober.aipp.TestUtils.mockReturnFlowTraceId;
import static com.huawei.fit.jober.aipp.TestUtils.mockTerminateFlow;
import static org.mockito.ArgumentMatchers.any;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.aipp.TestUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.character.ToolMessage;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.patterns.Agent;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolProvider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link LLMComponent}的测试集
 *
 * @author 易文渊
 * @since 2024-04-23
 */
@ExtendWith(MockitoExtension.class)
public class LLMComponentTest {
    @Mock
    private FlowInstanceService flowInstanceService;

    @Mock
    private MetaInstanceService metaInstanceService;

    @Mock
    private MetaService metaService;

    @Mock
    private ToolProvider toolProvider;

    @Mock
    private AippLogService aippLogService;

    @BeforeEach
    void setUp() {
        mockReturnFlowTraceId(metaInstanceService);
        Mockito.when(toolProvider.getTool(any())).thenReturn(Collections.emptyList());
    }

    private static Map<String, Object> buildLlmTestData() {
        Map<String, Object> businessData = new HashMap<>();
        // prompt
        HashMap<String, Object> prompt = new HashMap<>();
        prompt.put("template", "{{input}}");
        prompt.put("variables", new HashMap<String, String>() {{
            put("input", "good");
        }});
        businessData.put("prompt", prompt);
        businessData.put(AippConst.BS_AIPP_MEMORY_KEY, Collections.singletonList(new HashMap<String, String>() {{
            put("question", "q1");
            put("answer", "a1");
        }}));
        businessData.put("tools", Collections.emptyList());
        businessData.put("workflows", Collections.emptyList());
        businessData.put("model", "test_model");
        businessData.put("temperature", 0.7);
        businessData.put("systemPrompt", "");
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(new OperationContext()));
        businessData.put(AippConst.BS_AIPP_ID_KEY, "LLMComponentTest");
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, TestUtils.DUMMY_FLOW_INSTANCE_ID);
        return businessData;
    }

    @Test
    void shouldOkWhenNoTool() throws InterruptedException {
        // stub
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create()
                .map(m -> (Prompt) ChatMessages.from(new AiMessage("bad")))
                .close();
        Agent<Prompt, Prompt> agent = new Agent<Prompt, Prompt>(()->testAgent) {};
        LLMComponent llmComponent =
                new LLMComponent(flowInstanceService, metaInstanceService, metaService, toolProvider, agent, null);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService, metaService);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            InstanceDeclarationInfo info = ObjectUtils.cast(invocation.getArgument(2));
            Map<String, Object> value = info.getInfo().getValue();
            Assertions.assertEquals("bad", value.get("llmOutput"));
            return null;
        }).when(metaInstanceService).patchMetaInstance(any(), any(), any(), any());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    @Disabled
    void shouldFailedWhenNoTool() throws InterruptedException {
        // stub
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create().just(m -> {
            int err = 1 / 0;
        }).close();
        Agent<Prompt, Prompt> agent = new Agent<Prompt, Prompt>(()->testAgent) {};
        LLMComponent llmComponent = new LLMComponent(flowInstanceService,
                metaInstanceService,
                metaService,
                toolProvider,
                agent,
                aippLogService);

        // mock
        CountDownLatch countDownLatch = mockTerminateFlow(flowInstanceService, metaService, aippLogService);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            InstanceDeclarationInfo info = ObjectUtils.cast(invocation.getArgument(2));
            Map<String, Object> value = info.getInfo().getValue();
            Assertions.assertEquals("ERROR", value.get("inst_status"));
            return null;
        }).when(metaInstanceService).patchMetaInstance(any(), any(), any(), any());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    void shouldOkWhenUseWorkflowNoReturn() throws InterruptedException {
        // stub
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create()
                .just(m -> Assertions.assertEquals(4, m.messages().size()))
                .map(m -> (Prompt) ChatMessages.from(new ToolMessage("", "\"tool_async\"")))
                .close();
        Agent<Prompt, Prompt> agent = new Agent<Prompt, Prompt>(()->testAgent) {};
        LLMComponent llmComponent =
                new LLMComponent(flowInstanceService, metaInstanceService, metaService, toolProvider, agent, null);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService, metaService);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            InstanceDeclarationInfo info = ObjectUtils.cast(invocation.getArgument(2));
            Map<String, Object> value = info.getInfo().getValue();
            String childInstanceId = ObjectUtils.cast(value.get(AippConst.INST_CHILD_INSTANCE_ID));
            Assertions.assertEquals("tool_async", childInstanceId);
            Map<String, Object> businessData = new HashMap<>();
            businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
            businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, false);
            llmComponent.callback(TestUtils.buildFlowDataWithExtraConfig(businessData, null));
            return null;
        }).when(metaInstanceService).patchMetaInstance(any(), any(), any(), any());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }

    @Test
    void shouldOkWhenUseWorkflowNormalReturn() throws InterruptedException {
        // stub
        AtomicBoolean flag = new AtomicBoolean(false);
        AiProcessFlow<Prompt, Prompt> testAgent = AiFlows.<Prompt>create()
                .just(m -> {
                    List<? extends ChatMessage> messages = m.messages();
                    if (flag.get()) {
                        Assertions.assertEquals("tool_data", messages.get(messages.size() - 1).text());
                        Assertions.assertEquals(6, messages.size());
                    } else {
                        Assertions.assertEquals(4, messages.size());
                    }
                })
                .map(m -> {
                    ChatMessages chatMessages = ChatMessages.from(m.messages());
                    if (flag.get()) {
                        chatMessages.add(new AiMessage("bad"));
                    } else {
                        chatMessages.add(new AiMessage("", Collections.singletonList(new ToolCall())));
                        chatMessages.add(new ToolMessage("", "\"tool_async\""));
                    }
                    return (Prompt) chatMessages;
                })
                .just(m -> flag.set(true))
                .close();
        Agent<Prompt, Prompt> agent = new Agent<Prompt, Prompt>(()->testAgent) {};
        LLMComponent llmComponent =
                new LLMComponent(flowInstanceService, metaInstanceService, metaService, toolProvider, agent, null);

        // mock
        CountDownLatch countDownLatch = mockResumeFlow(flowInstanceService, metaService);

        Mockito.doAnswer((Answer<Void>) invocation -> {
            InstanceDeclarationInfo info = ObjectUtils.cast(invocation.getArgument(2));
            Map<String, Object> value = info.getInfo().getValue();
            String childInstanceId = ObjectUtils.cast(value.get(AippConst.INST_CHILD_INSTANCE_ID));
            if (childInstanceId != null) {
                Assertions.assertEquals("tool_async", childInstanceId);
                Map<String, Object> businessData = new HashMap<>();
                businessData.put(AippConst.BS_AIPP_FINAL_OUTPUT, "tool_data");
                businessData.put(AippConst.PARENT_INSTANCE_ID, TestUtils.DUMMY_FLOW_INSTANCE_ID);
                businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, true);
                llmComponent.callback(TestUtils.buildFlowDataWithExtraConfig(businessData, null));
            } else {
                Assertions.assertEquals("bad", value.get("llmOutput"));
            }
            return null;
        }).when(metaInstanceService).patchMetaInstance(any(), any(), any(), any());
        Mockito.when(toolProvider.getTool(any())).thenReturn(Collections.emptyList());

        // run
        llmComponent.handleTask(TestUtils.buildFlowDataWithExtraConfig(buildLlmTestData(), null));
        countDownLatch.await();
    }
}