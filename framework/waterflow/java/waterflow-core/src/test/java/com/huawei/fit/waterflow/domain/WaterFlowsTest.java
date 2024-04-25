/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain;

import static com.huawei.fit.waterflow.FlowsTestUtil.waitFortyMillis;
import static com.huawei.fit.waterflow.FlowsTestUtil.waitMillis;
import static com.huawei.fit.waterflow.FlowsTestUtil.waitSingle;
import static com.huawei.fit.waterflow.FlowsTestUtil.waitSize;
import static com.huawei.fit.waterflow.FlowsTestUtil.waitUntil;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.READY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocksMemo;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fit.waterflow.domain.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.domain.flow.Flows;
import com.huawei.fit.waterflow.domain.flow.ProcessFlow;
import com.huawei.fit.waterflow.domain.states.State;
import com.huawei.fit.waterflow.domain.stream.nodes.BlockToken;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.utils.Tuple;
import com.huawei.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * FlowsTest
 * 流程流转框架测试类
 *
 * @author g00564732
 * @since 1.0
 */
@DisplayName("流程实例核心引擎原始测试用例集合")
class WaterFlowsTest {
    static class TestData {
        private int f = 0;

        private int s = 0;

        private int t = 0;

        TestData first(int n) {
            this.f = n;
            return this;
        }

        TestData second(int n) {
            this.s = n;
            return this;
        }

        TestData third(int n) {
            this.t = n;
            return this;
        }

        /**
         * 总数
         *
         * @return 返回三个元素总和
         */
        public int total() {
            return f + s + t;
        }
    }

    @Nested
    @DisplayName("流程流转引擎基础测试用例集合")
    class WaterFlowBaseLineTest {
        private FlowContextRepo repo;

        private FlowContextMessenger messenger;

        private FlowLocks locks;

        @BeforeEach
        void setUp() {
            repo = new FlowContextMemoRepo();
            messenger = new FlowContextMemoMessenger();
            locks = new FlowLocksMemo();
        }

        @Test
        @DisplayName("流程实例map节点流转逻辑")
        void testFitStreamMapComputation() {
            long[] data = {1, 0};
            List<String> result = new ArrayList<>();
            ProcessFlow<Integer> flowTest = Flows.<Integer>create(repo, messenger, locks)
                    .id("flow test start node")
                    .map(i -> i + data[0]++)
                    .map(i -> i + data[0]++)
                    .close(r -> data[1] = r.get().getData());
            flowTest.setId("flow test");
            flowTest.offer(1);

            waitFortyMillis(() -> result);
            assertEquals("flow test", flowTest.getId());
            assertEquals(3, data[0]);
            assertEquals(4, data[1]);

            Flows.<Integer>create(repo, messenger, locks)
                    .map(i -> i + 1)
                    .map(i -> i * 2)
                    .map(i -> i.toString() + "-okay")
                    .close(r -> {
                        result.clear();
                        result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList()));
                    })
                    .offer(new Integer[] {1, 2, 3, 4, 5});
            waitSize(() -> result, 5);
            assertTrue(result.contains("12-okay"));
        }

        @Test
        @DisplayName("流程实例map节点结合block节点流转逻辑")
        void testFitStreamMapComputationWithBlock() {
            int j=0;
            while(j<100) {
                j++;
//                System.out.println("running "+(j++)+" .....");
                List<String> result = new ArrayList<>();
                BlockToken<Integer> block = new BlockToken<Integer>() {
                    @Override
                    public boolean verify(Integer data) {
                        return data > 6;
                    }
                };
                Flows.<Integer>create(repo, messenger, locks)
                        .map(i -> i + 1)
                        .map(i -> i * 2)
                        .block(block)
                        .map(i -> i.toString() + "-okay")
                        .close(r -> {
                            result.clear();
                            result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList()));
                        })
                        .offer(new Integer[]{1, 2, 3, 4, 5});
                waitUntil(() -> block.data().size() == 5);
                assertEquals(5, block.data().size());
                block.resume();
                waitUntil(() -> result.size() == 3);
                assertEquals(3, result.size());
            }
        }

        @Test
        @DisplayName("流程实例produce节点流转逻辑")
        void testFitStreamProduceComputation() {
            List<String> result = new ArrayList<>();
            Flows.<Integer>create(repo, messenger, locks)
                    .map((i -> i - 1))
                    .produce(l -> l.stream()
                            .sorted()
                            .limit(2)
                            .map(i -> i.toString() + "-okay")
                            .collect(Collectors.toList()))
                    //                    .close(i -> result.addAll(i.getAll().stream().map(FlowContext::getData).collect(Collectors.toList())))
                    .close(i -> result.addAll(i.get().getData()))
                    .offer(new Integer[] {1, 2, 3, 4, 5});
            waitSize(() -> result, 2);
            assertEquals(2, result.size());
        }

        @Disabled // todo 当前的window在计算origin时存在问题，待修复 @zhangqunhui
        @Test
        @DisplayName("流程实例produce节点结合block节点流转逻辑")
        void testFitStreamProduceComputationWithBlock() {
            List<String> result = new ArrayList<>();
            BlockToken<List<String>> block = new BlockToken<List<String>>() {
                @Override
                public boolean verify(List<String> data) {
                    return true;
                }
            };
            Flows.create(repo, messenger, locks)
                    .produce(l -> l.stream().limit(2).map(i -> i.toString() + "-okay").collect(Collectors.toList()))
                    .block(block)
                    .close(r -> {
                        result.addAll(r.get().getData());
                    })
                    .offer(new Integer[] {1, 2, 3, 4, 5});
            waitFortyMillis(Collections::emptyList);
            assertEquals(2, block.data().get(0).getData().size());
            assertEquals(0, result.size());

            block.resume();
            waitSize(() -> result, 2);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("流程实例reduce节点流转逻辑")
        void testFitStreamReduceComputation() {
            AtomicInteger counter = new AtomicInteger();
            ProcessFlow<Integer> flow = Flows.<Integer>create(repo, messenger, locks)
                    .map(i -> i * 2)
                    .reduce(0, (acc, i) -> acc + i)
                    .just(i -> counter.set(counter.get() + 1))
                    .close();
            // 数据发送为一个window
            flow.offer(new Integer[] {1, 2, 3, 4, 5});
            waitUntil(() -> counter.get() == 1);
            assertEquals(1, counter.get());

            // 单个发送没有window
            counter.set(0);
            for (int i = 0; i < 5; i++) {
                flow.offer(i);
            }
            waitUntil(() -> counter.get() == 5);
            assertEquals(5, counter.get());
        }

        @Test
        void test_reduce_with_window() {
            AtomicInteger counter = new AtomicInteger();
            Operators.Window<Integer> window = inputs -> inputs.size() == 2;

            ProcessFlow<Integer> flow = Flows.<Integer>create(repo, messenger, locks)
                    .window(window)
                    .reduce("", (acc, i) -> acc + i.toString())
                    .just(i -> {
                        System.out.println(i);
                        counter.set(counter.get() + 1);
                    })
                    .close();
            for (int i = 0; i < 6; i++) {
                flow.offer(i + 1);
            }

            waitUntil(() -> counter.get() == 3);
            assertEquals(3, counter.get());
        }

        @Test
        void test_reduce_with_window_and_keyBy() {
            AtomicInteger counter = new AtomicInteger();
            Operators.Window<Tuple<String, Tuple<String, Integer>>> window = inputs -> inputs.size() == 3;
            Flows.<Tuple<String, Integer>>create(repo, messenger, locks).keyBy(tuple -> tuple.first()).window(window)
                    //                    .<Tuple<String, Integer>>reduce((acc, i) -> acc == null ? Tuple.from(i.first(), i.second().second()) : Tuple.from(acc.first(), acc.second() + i.second().second()))
                    .reduce((Tuple<String, Integer>) Tuple.from("", 0),
                            (acc, i) -> Tuple.from(i.first(), acc.second() + i.second().second())).just(i -> {
                        System.out.println(i);
                        counter.set(counter.get() + 1);
                    }).close().offer(new Tuple[] {
                            Tuple.from("will", 1), Tuple.from("evan", 2), Tuple.from("will", 3), Tuple.from("evan", 4),
                            Tuple.from("will", 5), Tuple.from("evan", 6)
                    });

            waitUntil(() -> counter.get() == 4);
            assertEquals(4, counter.get());
        }

        @Test
        void test_buffer() {
            AtomicInteger counter = new AtomicInteger();
            Flows.<Integer>create(repo, messenger, locks).window(inputs -> inputs.size() == 3).buffer().flatMap(i -> {
                System.out.println("flat....." + i);
                return Flows.flux(i.toArray(new Integer[0]));
            }).just(i -> counter.set(counter.get() + 1)).close().offer(new Integer[] {1, 2, 3, 4, 5, 6});
            waitUntil(() -> counter.get() == 6);
        }

        @Test
        @DisplayName("流程实例parallel节点以及fork节点以及join节点all模式流转逻辑")
        void testFitStreamWithForkJoinAll() {
            TestData input = new TestData();
            List<TestData> output1 = new ArrayList<>();
            Flows.<TestData>create(repo, messenger, locks).just(i -> i.first(1).second(50).third(100)) // f=1;s=50;t=100
                    .just(i -> i.f--) // f=0;s=50;t=100
                    .parallel().fork(node -> node.just(i -> i.f += 4)) // f=4;s=50;t=100
                    .fork(node -> node.just(i -> i.s--) // f=4;s=49;t=100
                            .just(i -> i.s += 5)) // f=4;s=54;t=100
                    .fork(node -> node.just(i -> i.t += 2)) // f=4;s=54;t=102
                    .join(input, (acc, data) -> data).close(r -> output1.add(r.get().getData())).offer(input);
            waitSingle(() -> output1);
            assertEquals(160, output1.get(0).total());
        }

        @Test
        @DisplayName("流程实例parallel节点以及fork节点以及join节点either模式流转逻辑")
        void testFitStreamWithForkJoinEither() {
            //            TestData input = new TestData();
            //            List<TestData> output = new ArrayList<>();
            //            ValidatorBlock<TestData> block = new ValidatorBlock<>();
            //            Flows.<TestData>create(repo, messenger, locks)
            //                    .parallel(EITHER)
            //                    .fork(node -> node.just(i -> i.first(100)))
            //                    .fork(node -> node.just(i -> i.second(200)).block(block))
            //                    .join()
            //                    .close(r -> output.add(r.get().getData()))
            //                    .offer(input);
            //            waitSingle(() -> output);
            //            assertEquals(100, output.get(0).f);
            //            assertEquals(0, output.get(0).s);
            //            block.process(new ArrayList<>());
            //            //either 已经失效，所以这个block没用
            //            assertEquals(0, output.get(0).s);
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testConditionsMatchTo() {
            TestData input = new TestData();
            List<TestData> output = new ArrayList<>();
            ProcessFlow<TestData> flow = Flows.<TestData>create(repo, messenger, locks)
                    .just(i -> i.f++).id("plusF")
                    .just(i -> i.s++).id("plusS")
                    .conditions()
                    .matchTo(i -> i.f < 20, node -> node.to("plusF"))
                    .matchTo(i -> i.s < 20, node -> node.to("plusS"))
                    .others(i -> i)
                    .close(r -> output.add(r.get().getData()));
            flow.offer(input.first(11).second(0).third(0));
            waitSingle(() -> output);
            assertEquals(20, output.get(0).f);
            assertEquals(20, output.get(0).s);
            assertEquals(0, output.get(0).t);
            output.clear();
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testConditionsMatchToAndMatch() {
            TestData input = new TestData();
            List<TestData> output = new ArrayList<>();
            ProcessFlow<TestData> flow = Flows.<TestData>create(repo, messenger, locks)
                    .just(i -> i.t++).id("plusT")
                    .just(i -> i.f++).id("plusF")
                    .just(i -> i.s++).id("plusS")
                    .conditions()
                    .matchTo(i -> i.f < 20, node -> node.to("plusF"))
                    .matchTo(i -> i.s < 20, node -> node.to("plusS"))
                    .match(i -> i.t < 5, node -> node.just(i -> i.t += 5))
                    .matchTo(i -> i.t < 20, node -> node.to("plusT"))
                    .others(i -> i)
                    .close(r -> output.add(r.get().getData()));
            flow.offer(input.first(11).second(0).third(0));
            waitSingle(() -> output);
            assertEquals(20, output.get(0).f);
            assertEquals(20, output.get(0).s);
            assertEquals(6, output.get(0).t);
            output.clear();

            flow.offer(input.first(11).second(0).third(12));
            waitSingle(() -> output);
            assertEquals(27, output.get(0).f);
            assertEquals(27, output.get(0).s);
            assertEquals(20, output.get(0).t);
            output.clear();
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testFitStreamWithConditionNewJust() {
            TestData input = new TestData();
            List<TestData> output = new ArrayList<>();
            // test conditions and others for just
            ProcessFlow<TestData> flow = Flows.<TestData>create(repo, messenger, locks)
                    .conditions()
                    .match(i -> i.f > 10, flowNode -> flowNode.just(value -> {
                        value.f++;
                    }).map(value -> {
                        value.f += 10;
                        return value;
                    }))
                    .match(i -> i.s > 10, flowNode -> flowNode.just(value -> {
                        value.s++;
                    }))
                    .others(flowNode -> flowNode.map(value -> {
                        value.t++;
                        return value;
                    }))
                    .close(r -> output.add(r.get().getData()));
            flow.offer(input.first(11).second(0).third(0));
            waitSingle(() -> output);
            assertEquals(22, output.get(0).f);
            assertEquals(0, output.get(0).s);
            assertEquals(0, output.get(0).t);
            output.clear();

            flow.offer(input.first(0).second(11).third(0));
            waitSingle(() -> output);
            assertEquals(0, output.get(0).f);
            assertEquals(12, output.get(0).s);
            assertEquals(0, output.get(0).t);
            output.clear();

            flow.offer(input.first(0).second(0).third(11));
            waitSingle(() -> output);
            assertEquals(0, output.get(0).f);
            assertEquals(0, output.get(0).s);
            assertEquals(12, output.get(0).t);
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testFitStreamWithConditionsTo() {
            TestData input = new TestData();
            List<Integer> output = new ArrayList<>();
            // test conditions and others for just
            ProcessFlow<TestData> flow = Flows.<TestData>create(repo, messenger, locks)
                    .just(i -> i.t++)
                    .id("tPlus")
                    .just(i -> i.t += 20)
                    .id("tPlusPlus")
                    .conditions()
                    .matchTo(i -> i.t < 20, node -> node.to("tPlus"))
                    .match(i -> i.f > 10, flowNode -> flowNode.map(value -> ++value.f))
                    .match(i -> i.s > 10,
                            flowNode -> flowNode.conditions().match(i -> i.f > 0, node -> node.just(value -> {
                                value.t++;
                            })).others(node -> node.just(value -> {
                                value.t--;
                            })).map(value -> value.t))
                    .matchTo(i -> i.t < 50, node -> node.just(i -> i.f++).to("tPlusPlus"))
                    .others(flowNode -> flowNode.map(value -> {
                        return ++value.t;
                    }))
                    .close(r -> output.add(r.get().getData()));
            flow.offer(input.first(11).second(0).third(0));
            waitSingle(() -> output);
            assertEquals(12, output.get(0));
            output.clear();

            flow.offer(input.first(-10).second(12).third(0));
            waitSingle(() -> output);
            assertEquals(20, output.get(0));
            output.clear();

            flow.offer(input.first(0).second(0).third(-5));
            waitSingle(() -> output);
            assertEquals(58, output.get(0));
        }

        // todo 1. dataStart 需要wrapper
        // 2. match 去掉context
        // 3. close里面的context也去掉，不暴露
        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testFitStreamWithConditions() {
            TestData input = new TestData();
            List<Integer> output = new ArrayList<>();
            // test conditions and others for just
            ProcessFlow<TestData> flow = Flows.<TestData>create(repo, messenger, locks)
                    .conditions()
                    .match(i -> i.f > 10, flowNode -> flowNode.map(value -> ++value.f))
                    .match(i -> i.s > 10,
                            flowNode -> flowNode.conditions().match(i -> i.f > 0, node -> node.just(value -> {
                                value.t++;
                            })).others(node -> node.just(value -> {
                                value.t--;
                            })).map(value -> value.t))
                    .others(flowNode -> flowNode.map(value -> {
                        return ++value.t;
                    }))
                    .close(r -> output.add(r.get().getData()));
            flow.offer(input.first(11).second(0).third(0));
            waitSingle(() -> output);
            assertEquals(12, output.get(0));
            output.clear();

            flow.offer(input.first(10).second(12).third(0));
            waitSingle(() -> output);
            assertEquals(1, output.get(0));
            output.clear();

            flow.offer(input.first(-10).second(12).third(0));
            waitSingle(() -> output);
            assertEquals(-1, output.get(0));
            output.clear();

            flow.offer(input.first(0).second(0).third(13));
            waitSingle(() -> output);
            assertEquals(14, output.get(0));
        }

        @Test
        @DisplayName("流程实例condition节点以及match节点以及others节点流转逻辑")
        void testFitStreamWithConditionsEmptyOthers() {
            TestData input = new TestData();
            List<Integer> output = new ArrayList<>();
            // test conditions and others for just
            ProcessFlow<TestData> flow = Flows.<TestData>create(repo, messenger, locks)
                    .conditions()
                    .match(i -> i.f > 10, flowNode -> flowNode.map(value -> ++value.f))
                    .others()
                    .close(r -> output.add(r.get().getData()));

            flow.offer(input.first(0).second(0).third(0));
            waitUntil(() -> false);
            assertEquals(0, output.size());

            TestData input2 = new TestData();
            flow.offer(input2.first(11).second(0).third(0));
            waitUntil(() -> false);
            assertEquals(1, output.size());
            assertEquals(12, output.get(0));
        }

        @Test
        @DisplayName("流程实例source数据源")
        void testDataDrivenSource() {
            AtomicInteger counter = new AtomicInteger();
            Emitter<Integer, FlowSession> emitter = new Emitter<Integer, FlowSession>() {
                private EmitterListener<Integer, FlowSession> handler;

                @Override
                public void register(EmitterListener<Integer, FlowSession> handler) {
                    this.handler = handler;
                }

                @Override
                public void emit(Integer data, FlowSession trance) {
                    this.handler.handle(data, trance);
                }
            };
            // source: 数据发射源
            Flows.source(emitter).map(n -> n + 10).just(counter::set).offer();
            emitter.emit(10);
            waitFortyMillis(Collections::emptyList);
            assertEquals(20, counter.get());
        }

        @Test
        @DisplayName("流程实例just节点mono模式流转逻辑")
        void testFitStreamCreateFromSingleDataProducer() {
            List<TestData> output = new ArrayList<>();
            Flows.mono(new TestData().first(10).second(10).third(10)).just(i -> i.f++).just(i -> output.add(i)).offer();
            waitSingle(() -> output);
            assertEquals(11, output.get(0).f);
        }

        @Test
        @DisplayName("流程实例reduce节点flux模式流转逻辑")
        void testFitStreamCreateFromListDataProducer() {
            //            Integer[] output = {0};
            //            Flows.flux(1, 2, 7)
            //                    .reduce(list -> list.stream().reduce(Integer::sum).get())
            //                    .just(r -> output[0] = r)
            //                    .offer();
            //            waitFortyMillis(Collections::emptyList);
            //            assertEquals(10, (long) output[0]);
        }

        @Test
        @Disabled(
                "流程之间跳转存在两个问题：1、版本streamId不一样，导致跳转到目标流程根据版本查不到数据；2、跳转的节点存在多个to，并发无法控制")
        @DisplayName("流程实例just节点以及alias节点流转逻辑")
        void testFitStreamCrossStream() {
            AtomicBoolean f1Result = new AtomicBoolean(false);
            AtomicInteger f2Result = new AtomicInteger(0);
            AtomicInteger f3Result = new AtomicInteger(0);
            // Flow 1;
            //            AtomicStateAlias<TestData, TestData> act1 = new AtomicStateAlias<>();
            //            AtomicStateAlias<TestData, TestData> act2 = new AtomicStateAlias<>();
            Flows.<TestData>create(repo, messenger, locks).just(d -> d.t++)
                    //.alias(w -> act1.set(w))//設置別名，為其他flow引用
                    .just(d -> d.f++)
                    //                    .alias(w -> act2.set(w))
                    .just(d -> d.s++).close(r -> {
                        f1Result.set(true);
                        f3Result.set(r.get().getData().total());
                    });

            // Flow 2
            //            AtomicStateAlias<Integer, Integer> act3 = new AtomicStateAlias<>();
            ProcessFlow<Integer> flow2 = Flows.<Integer>create(repo, messenger, locks)
                    //.when(i -> new TestData().first(i))
                    //.to(act1.get())
                    //.when(i -> i, i -> false)
                    .map(i -> i + 100)
                    //.alias(w -> act3.set(w))
                    .close(r -> f2Result.set(r.get().getData()));

            // begin to offer data without act2 come back
            flow2.offer(100);
            waitMillis(Collections::emptyList, 50);
            assertTrue(f1Result.get());
            assertEquals(0, f2Result.get());
            assertEquals(103, f3Result.get());

            // offer data with act2 come back
            f1Result.set(false);
            //            act2.get().to(act3.get(), d -> d.f + d.t);//send the data back
            flow2.offer(100);
            waitFortyMillis(Collections::emptyList);
            assertFalse(f1Result.get());
            assertEquals(202, f2Result.get());
        }

        @Test
        @Disabled(
                "流程之间跳转存在两个问题：1、版本streamId不一样，导致跳转到目标流程根据版本查不到数据；2、跳转的节点存在多个to，并发无法控制")
        @DisplayName("流程实例垮流程节点流转逻辑")
        void testFitStreamNotifyCrossStream() {
            AtomicInteger f1Result = new AtomicInteger(0);
            AtomicInteger f2Result = new AtomicInteger(0);
            // Flow 1;
            Flows.<TestData>create(repo, messenger, locks).just(d -> d.t++)
                    //                    .alias(w -> act1.set(w))//設置別名，為其他flow引用
                    .just(d -> d.f++).just(d -> d.s++).close(r -> f1Result.set(r.get().getData().total()));

            // Flow 2
            // act1.get()的节点在其自身流程中有一个to节点(just(d -> d.f++))
            // 在跳转流程中有一个to节点(close(r -> f2Result.set(r.get())))
            // 并发会导致一个context同时发送到两个节点处理，中间状态错乱
            // when.to的方式同时只允许一条边是满足条件的，以下用例不正确
            Flows.<Integer>create(repo, messenger, locks)
                    //.when(i -> new TestData().first(i))
                    //.to(act1.get())
                    //.when(i -> i, i -> true)
                    .map(i -> i + 100).close(r -> f2Result.set(r.get().getData())).offer(100);

            waitFortyMillis(Collections::emptyList);
            assertEquals(103, f1Result.get());
            assertEquals(200, f2Result.get());
        }

        @Test
        @DisplayName("流程实例异常处理流转逻辑")
        void testExceptionHandleForFitStream() {
            AtomicReference<TestData> output = new AtomicReference<>();
            // 单节点错误处理
            Flows.<TestData>create(repo, messenger, locks).just(t -> t.first(100)).just(t -> {
                if (t.f < 120) {
                    throw new RuntimeException();
                } else {
                    t.second(100);
                }
            }).error((e, b, d) -> {
                d.get(0).getData().first(120);
                d.forEach(c -> c.setStatus(READY));
                b.retry(d);
            }).close(r -> {
                output.set(r.get().getData());
            }).offer(new TestData());
            waitUntil(() -> output.get() != null, 2000);
            //            waitFortyMillis(Collections::emptyList);
            assertEquals(120, output.get().f);
            assertEquals(100, output.get().s);

            // 整体错误处理
            Flows.<TestData>create(repo, messenger, locks).just(t -> t.first(100)).just(t -> {
                if (t.f < 120) {
                    throw new RuntimeException();
                } else {
                    t.second(100);
                }
            }).close(r -> output.set(r.get().getData()), (e, b, d) -> {
                ObjectUtils.<TestData>cast(d.get(0).getData()).first(120);
                d.forEach(c -> c.setStatus(READY));
                b.retry(d);
            }).offer(new TestData());
            waitFortyMillis(Collections::emptyList);
            assertEquals(120, output.get().f);
            assertEquals(100, output.get().s);
        }

        @Test
        void test_flow_subscribe_flow() {
            AtomicInteger out1 = new AtomicInteger();
            // 第一条流:start->node1->end
            ProcessFlow<Integer> flow1 = Flows.<Integer>create().map(i -> i.toString()).close();
            // 第二条流:start->node->handle_node->end
            ProcessFlow<String> flow2 = Flows.<String>create()
                    .map(s -> s + "00")
                    .id("sender")
                    .map(s -> Integer.parseInt(s) + 100)
                    .close(r -> out1.set(r.get().getData()));
            // 从中间节点offer数据flow1.end节点的数据
            flow2.offer("sender", flow1);
            flow1.offer(100);
            waitFortyMillis(Collections::emptyList);
            assertEquals(200, out1.get());

            AtomicReference<String> out2 = new AtomicReference<>();
            AtomicReference<String> out3 = new AtomicReference<>();
            State<Integer, Integer, Integer, ProcessFlow<Integer>> node1 = Flows.<Integer>create().map(i -> i + 100);
            flow1 = node1.just(i -> out2.set(i.toString() + "flow1")).close();
            Flows.<Integer>create().just(i -> out3.set(i.toString() + "flow2")).close().offer(node1);
            flow1.offer(100);
            waitFortyMillis(Collections::emptyList);
            assertEquals("200flow1", out2.get());
            assertEquals("200flow2", out3.get());
        }

        @Test
        void test_node_subscribe_data() {
            AtomicInteger out1 = new AtomicInteger();
            ProcessFlow<String> flow2 = Flows.<String>create()
                    .map(s -> s + "00")
                    .id("sender")
                    .map(s -> Integer.parseInt(s) + 100)
                    .close(r -> out1.set(r.get().getData()));
            flow2.offer("2");
            waitFortyMillis(Collections::emptyList);
            assertEquals(300, out1.get());
            out1.set(0);

            // 从中间节点offer数据
            flow2.offer("sender", "100");
            waitFortyMillis(Collections::emptyList);
            assertEquals(200, out1.get());
        }

        @Test
        void test_process() {
            AtomicInteger counter = new AtomicInteger();
            Flows.<Integer>create()
                    .<Integer>process((data, ctx, collector) -> {
                        ctx.setState("me", "will");
                        collector.collect(10);
                        collector.collect(20);
                        collector.collect(30);
                        collector.collect(40);
                        collector.collect(50);
                    })
                    .map(i -> i.toString())
                    .map((data, ctx) -> data + ctx.getState("me").toString())
                    .just(d -> System.out.println(d))
                    .close(r -> counter.set(counter.get() + 1))
                    .offer(0);
            waitUntil(() -> counter.get() >= 5);
            assertEquals(5, counter.get());
        }

        @Test
        void test_flow_flat_map() {
            List<String> result = new ArrayList<>();
            Flows.<Integer>create().flatMap(num -> {
                String[] maps = new String[num];
                for (int i = 0; i < num; i++) {
                    maps[i] = "flat map ";
                }
                return Flows.flux(maps);
            }).just(c -> result.add(c)).close().offer(4);
            waitUntil(() -> result.size() == 4);
            assertEquals(4, result.size());
        }

        private <T> Supplier<List<FlowContext<T>>> contextSupplier(FlowContextRepo<T> repo, String traceId,
                String metaId, FlowNodeStatus status) {
            return () -> {
                List<FlowContext<T>> all = repo.getContextsByTrace(traceId);
                return all.stream()
                        .filter(c -> c.getPosition().equals(metaId))
                        .filter(c -> c.getStatus() == status)
                        .collect(Collectors.toList());
            };
        }
    }
}