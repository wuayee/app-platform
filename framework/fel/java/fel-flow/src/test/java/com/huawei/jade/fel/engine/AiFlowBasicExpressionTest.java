/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine;

import static com.huawei.jade.fel.utils.FlowsTestUtils.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.utils.AiFlowTestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
                    .just(input -> input.setFirst(1))
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
                    .just((input, ctx) -> input.setFirst(input.getFirst() + ctx.<Integer>getState("key0")))
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
}
