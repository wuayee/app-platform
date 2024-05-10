/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine;

import static com.huawei.jade.fel.utils.FlowsTestUtils.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fit.waterflow.domain.context.StateContext;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.utils.Mermaid;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.utils.AiFlowTestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
        void shouldOkWhenOfferDataWithNormalConverse() throws InterruptedException {
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
        void shouldOkWhenOfferDataWithBindingCustomContext() throws InterruptedException, TimeoutException {
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
        void shouldOkWhenReduceWithoutWindow() throws InterruptedException, TimeoutException {
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
        void test_process_with_custom_state() throws InterruptedException, TimeoutException {
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
        void testConditionsMatchTo() throws InterruptedException {
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
        void testConditionsMatchToAndMatch() throws InterruptedException {
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

        @Test
        @DisplayName("通过mermaid格式图形还原ai flow的流程设计")
        void testMermaidToCreateChart() {
            ChatBlockModel model =
                    new ChatBlockModel<>(prompts -> new FlatChatMessage(new AiMessage("model answer")));
            AiProcessFlow flow = AiFlows.create()
                    .prompt(null)
                    .delegate((input, context) -> null)
                    .window(inputs -> false)
                    .keyBy(input -> null)
                    .generate(model).close();

            assertThat(new Mermaid(flow.baseFlow()).get()).isEqualTo("st((Start))\n" +
                    "st-->n0(prompt)\n" +
                    "n0-->n1(delegate to pattern)\n" +
                    "n1-->n2(window)\n" +
                    "n2-->n3(key by)\n" +
                    "n3-->n4(generate)\n" +
                    "n4-->e((End))\n");

            //在markdown里使用以下脚本显示mermaid
            //```mermaid
            // mermaid.get();//测试用例里的输出
            // ```
        }
    }
}
