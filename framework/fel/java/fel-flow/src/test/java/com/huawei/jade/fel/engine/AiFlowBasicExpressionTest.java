/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine;

import static com.huawei.jade.fel.utils.FlowsTestUtils.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fit.waterflow.domain.utils.Mermaid;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.engine.operators.patterns.FlowSupportable;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.utils.AiFlowTestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI 流程基础表达式的测试。
 *
 * @author 刘信宏
 * @since 2024-04-29
 */
@DisplayName("测试 AiFlow 的基础表达式")
public class AiFlowBasicExpressionTest {
    @Nested
    @DisplayName("测试基础数据处理表达式")
    class BasicDataProcess {
        @Test
        @DisplayName("普通会话，map和just节点成功消费数据")
        void shouldOkWhenOfferDataWithNormalConverse() {
            AiProcessFlow<AiFlowTestData, String> flow = AiFlows.<AiFlowTestData>create()
                    .just(input -> input.getFirst().set(1))
                    .map(input -> String.valueOf(input.total()))
                    .close();
            StringBuilder answer = new StringBuilder();
            flow.converse().doOnSuccess(answer::append).offer(new AiFlowTestData()).await();
            assertEquals("1", answer.toString());
        }

        @Test
        @DisplayName("绑定自定义上下文，map和just节点成功消费自定义信息")
        void shouldOkWhenOfferDataWithBindingCustomContext() {
            AiProcessFlow<AiFlowTestData, String> flow = AiFlows.<AiFlowTestData>create()
                    .just((input, ctx) -> input.getFirst().getAndAdd(ctx.<Integer>getState("key0")))
                    .map((input, ctx) -> String.valueOf(input.total()) + ctx.getState("key1"))
                    .<String>process(((input, ctx, collector) -> {
                        collector.collect(input);
                    }))
                    .close();

            StringBuilder answer = new StringBuilder();
            flow.converse()
                    .bind("key0", 2)
                    .bind("key1", "value1")
                    .doOnSuccess(answer::append)
                    .offer(new AiFlowTestData())
                    .await(500, TimeUnit.MILLISECONDS);
            assertEquals("2value1", answer.toString());
        }
    }

    @Nested
    @DisplayName("测试数据聚合相关表达式")
    class GatherData {
        @Test
        @DisplayName("无window的reduce数据聚合")
        void shouldOkWhenReduceWithoutWindow() {
            AiProcessFlow<Integer, Integer> flow = AiFlows.<Integer>create()
                    .reduce(0, ((acc, input) -> {
                        acc += input;
                        return acc;
                    })).close();

            List<Integer> counters = new ArrayList<>();
            // 逐个注入，reduce不起作用
            for (int i = 0; i < 4; i++) {
                flow.converse().doOnSuccess(counters::add).offer(i + 1);
            }
            waitUntil(() -> counters.size() == 4, 1000);
            assertEquals(4, counters.size());

            counters.clear();
            // 批量注入会将同一批次的聚合为一个
            flow.converse().doOnSuccess(counters::add).offer(1, 2, 3, 4)
                    .await(500, TimeUnit.MILLISECONDS);
            assertThat(counters).hasSize(1).contains(10);
        }

        @Test
        @DisplayName("带有window的reduce数据聚合")
        void shouldOkWhenReduceWithWindow() {
            AiProcessFlow<Integer, Integer> flow = AiFlows.<Integer>create()
                    .window(inputs -> inputs.size() == 2)
                    .reduce(0, ((acc, input) -> {
                        acc += input;
                        return acc;
                    })).close();

            checkInjectDataOneByOne(flow);
        }

        @Test
        @DisplayName("buffer数据聚合, 逐个注入数据")
        void shouldOkWhenGatherWithBuffer() {
            AiProcessFlow<Integer, Integer> flow = AiFlows.<Integer>create()
                    .buffer(2)
                    .map(input -> input.stream().reduce(0, Integer::sum))
                    .close();

            checkInjectDataOneByOne(flow);
        }

        @Test
        @DisplayName("带有process节点的buffer数据聚合")
        void test_process_with_custom_state() {
            StringBuffer answer = new StringBuffer(512);

            AiFlows.<Integer>create()
                    .<Integer>process((data, ctx, collector) -> {
                        ctx.setState("key1", "value1");
                        collector.collect(data);
                        collector.collect(data + 1);
                    })
                    .map((data, ctx) -> data.toString() + ctx.getState("key0"))
                    .map((data, ctx) -> data + ctx.getState("key1"))
                    .buffer(2)
                    .close()
                    .converse().bind("key0", "value0")
                    .doOnSuccess(data -> answer.append(String.join("\n", data)))
                    .offer(5)
                    .await(500, TimeUnit.MILLISECONDS);

            assertEquals("5value0value1\n6value0value1", answer.toString());
        }

        private void checkInjectDataOneByOne(AiProcessFlow<Integer, Integer> flow) {
            // 逐个注入
            List<Integer> counters = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < 4; i++) {
                flow.converse().doOnSuccess(counters::add).offer(i + 1);
            }
            waitUntil(() -> counters.size() == 2, 1000);
            assertEquals(2, counters.size());
            assertEquals(3, counters.get(0));
            assertEquals(7, counters.get(1));
        }
    }

    @Nested
    @DisplayName("测试条件分支")
    class ConditionBranchTest {
        @Test
        void test_create_ai_flow_conditions() {
            AtomicInteger result = new AtomicInteger(0);
            AiFlows.<String>create()
                    .map(data -> data + "1").id("plus1")
                    .map(data -> data + "2").id("plus2")
                    .conditions()
                    .matchTo(data -> data.length() < 4, node -> node.to("plus1"))
                    .match(data -> data.length() > 5, node -> node.map(i -> Integer.parseInt(i)))
                    .matchTo(data -> data.length() < 10, node -> node.just(i -> {
                    }).to("plus2"))
                    .others()
                    .close(i -> result.set(i.get().getData()))
                    .converse()
                    .offer("3");
            waitUntil(() -> result.get() != 0);
            assertEquals(312122, result.get());
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testConditionsMatchTo() {
            List<AiFlowTestData> output = new ArrayList<>();
            AiProcessFlow<AiFlowTestData, AiFlowTestData> flow = AiFlows.<AiFlowTestData>create()
                    .just(data -> data.getFirst().incrementAndGet()).id("plusF")
                    .just(data -> data.getSecond().incrementAndGet()).id("plusS")
                    .conditions()
                    .matchTo(data -> data.getFirst().get() < 20, node -> node.to("plusF"))
                    .matchTo(data -> data.getSecond().get() < 20, node -> node.to("plusS"))
                    .others(i -> i)
                    .close(r -> output.add(r.get().getData()));
            flow.converse().offer(new AiFlowTestData(11, 0, 0)).await();
            assertThat(output).hasSize(1);
            assertThat(output.get(0).getFirst().get()).isEqualTo(20);
            assertThat(output.get(0).getSecond().get()).isEqualTo(20);
            assertThat(output.get(0).getThird().get()).isEqualTo(0);
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testConditionsMatchToAndMatch() {
            List<AiFlowTestData> output = new ArrayList<>();
            AiProcessFlow<AiFlowTestData, AiFlowTestData> flow = AiFlows.<AiFlowTestData>create()
                    .just(data -> data.getThird().incrementAndGet()).id("plusT")
                    .just(data -> data.getFirst().incrementAndGet()).id("plusF")
                    .just(data -> data.getSecond().incrementAndGet()).id("plusS")
                    .conditions()
                    .matchTo(data -> data.getFirst().get() < 20, node -> node.to("plusF"))
                    .matchTo(data -> data.getSecond().get() < 20, node -> node.to("plusS"))
                    .match(data -> data.getThird().get() < 5, node -> node.just(data -> data.getThird().getAndAdd(5)))
                    .matchTo(data -> data.getThird().get() < 20, node -> node.to("plusT"))
                    .others(i -> i)
                    .close(r -> output.add(r.get().getData()));
            flow.converse().offer(new AiFlowTestData(11, 0, 0)).await();

            assertThat(output).hasSize(1);
            assertThat(output.get(0).getFirst().get()).isEqualTo(20);
            assertThat(output.get(0).getSecond().get()).isEqualTo(20);
            assertThat(output.get(0).getThird().get()).isEqualTo(6);
            output.clear();

            flow.converse().offer(new AiFlowTestData(11, 0, 12)).await();
            assertThat(output).hasSize(1);
            assertThat(output.get(0).getFirst().get()).isEqualTo(27);
            assertThat(output.get(0).getSecond().get()).isEqualTo(27);
            assertThat(output.get(0).getThird().get()).isEqualTo(20);
            output.clear();
        }
    }

    /**
     * 在 markdown 里使用以下脚本显示 mermaid 流程图。
     * ```mermaid
     * graph LR
     * mermaid.get(); // 测试用例里的输出
     * ```
     */
    @Nested
    @DisplayName("测试解析mermaid格式的流程结构")
    class MermaidTest {
        private final ChatBlockModel<Prompt> model =
                new ChatBlockModel<>(prompts -> new FlatChatMessage(new AiMessage("model answer")));

        private final AiProcessFlow<Prompt, AiMessage> subFlow = AiFlows.<Prompt>create()
                .just(((input, context) -> {}))
                .generate(this.model).id("llm")
                .delegate((input, session) -> input)
                .conditions()
                .match(input -> true, node -> node.map(data -> data))
                .matchTo(AiMessage::isToolCall, node -> node.map(data -> data).to("llm"))
                .others()
                .close();

        private void checkMermaid(String mermaidStr, String expected) {
            String[] expectedSplit = expected.split("\n");
            String[] split = mermaidStr.split("\n");
            assertThat(split).hasSize(expectedSplit.length);
            for (String str : split) {
                assertThat(str).isIn((Object[]) expectedSplit);
            }
        }

        @Test
        @DisplayName("通过mermaid格式图形还原ai flow的流程设计")
        void testMermaidToCreateChart() {
            AiProcessFlow<Object, AiMessage> flow = AiFlows.create()
                    .delegate((input, context) -> null)
                    .window(inputs -> false)
                    .keyBy(input -> null)
                    .prompt(Prompts.history())
                    .generate(this.model)
                    .close();

            String expected = "start((Start))\n" + "start-->node0(delegate to pattern)\n" + "node4-->end5((End))\n"
                    + "node3-->node4(generate)\n" + "node2-->node3(prompt)\n" + "node1-->node2(key by)\n"
                    + "node0-->node1(window)";
            checkMermaid(new Mermaid(flow.origin()).get(), expected);
        }

        @Test
        @DisplayName("解析带有委托子流程的流程结构")
        void shouldOkWhenParseMermaidWithDelegateFlowPattern() {
            AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                    .prompt(Prompts.history(), Prompts.human("{{0}}"))
                    .delegate(new FlowSupportable<>(() -> this.subFlow))
                    .map(AiMessage::text)
                    .close();

            String expected = "start((Start))\n" + "sub_start4-->node5(just)\n" + "start-->node0(prompt)\n"
                    + "node9-->node10([+])\n" + "node8-->node9(map)\n" + "node8-->node12(map)\n" + "node7-->node8{?}\n"
                    + "node6-->node7(delegate to pattern)\n" + "node5-->node6(llm)\n" + "node2-->end3((End))\n"
                    + "node12-->node6\n" + "node10-->end11((End))\n" + "node1-. delegate .->sub_start4((Start))\n"
                    + "node1-->node2(map)\n" + "node0-->node1(delegate to flow)\n"
                    + "end11-. emit .->node1(delegate to flow)";
            checkMermaid(new Mermaid(flow.origin()).get(), expected);
        }

        @Test
        @DisplayName("解析带有委托子流程指定节点的流程结构")
        void shouldOkWhenParseMermaidWithDelegateFlowSpecifyNode() {
            AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                    .prompt(Prompts.history(), Prompts.human("{{0}}"))
                    .delegate(this.subFlow, "llm")
                    .map(AiMessage::text)
                    .close();

            String expected = "start((Start))\n" + "sub_start4-->node6(just)\n" + "sub_start4((Start))\n"
                    + "start-->node0(prompt)\n" + "node9-->node10([+])\n" + "node8-->node9(map)\n"
                    + "node8-->node12(map)\n" + "node7-->node8{?}\n" + "node6-->node5\n"
                    + "node5-->node7(delegate to pattern)\n" + "node2-->end3((End))\n" + "node12-->node5\n"
                    + "node10-->end11((End))\n" + "node1-. delegate .->node5(llm)\n" + "node1-->node2(map)\n"
                    + "node0-->node1(delegate to node)\n" + "end11-. emit .->node1(delegate to node)";
            checkMermaid(new Mermaid(flow.origin()).get(), expected);
        }
    }
}
