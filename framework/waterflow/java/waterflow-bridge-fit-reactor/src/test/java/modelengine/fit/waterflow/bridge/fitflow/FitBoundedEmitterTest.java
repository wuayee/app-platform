/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.bridge.fitflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.flow.Flows;
import modelengine.fit.waterflow.domain.flow.ProcessFlow;
import modelengine.fit.waterflow.domain.stream.objects.ThreadMode;
import modelengine.fit.waterflow.domain.utils.FlowDebug;
import modelengine.fit.waterflow.domain.utils.SleepUtil;
import modelengine.fit.waterflow.domain.utils.UUIDUtil;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Publisher;

import lombok.Data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * 测试类
 */
class FitBoundedEmitterTest {
    private static void waitUntil(Supplier<Boolean> stop, int most) {
        int time = 0;
        int step = 10;
        while (!stop.get() && time < most) {
            SleepUtil.sleep(step);
            time += step;
        }
    }

    private static class TestEmitter<O, D> extends FitBoundedEmitter<O, D> {
        private final AtomicInteger count = new AtomicInteger(0);

        /**
         * 通过数据发布者和有限流数据构造器初始化 {@link TestEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
         *
         * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
         * @param builder 表示有限流数据构造器的 {@link Function}{@code <}{@link O}{@code ,
         * }{@link D}{@code >}。
         */
        public TestEmitter(FlowSession flowSession, Publisher<O> publisher, Function<O, D> builder) {
            super(flowSession, publisher, builder);
        }

        @Override
        protected void errorAction(Exception cause) {
            count.incrementAndGet();
        }

        @Override
        protected void completedAction() {
            count.incrementAndGet();
        }

        @Override
        protected void consumeAction(O source, D target) {
            count.incrementAndGet();
        }
    }

    @Test
    void baseTest() {
        AtomicBoolean end = new AtomicBoolean(false);
        List<String> result = new ArrayList<>();
        Publisher<String> publisher = subscriber -> new Thread(() -> {
            subscriber.consume("1");
            subscriber.consume("2");
            subscriber.complete();
        }).start();
        Function<String, TestEmitterData<String>> builder = new TestBoundedEmitterDataBuilder<>();
        TestEmitter<String, TestEmitterData<String>> emitter = new TestEmitter<>(new FlowSession(), publisher, builder);
        emitter.register((data, token) -> {
            result.add(data.getData());
        });
        waitUntil(end::get, 1000);
        assertEquals(2, result.size());
    }

    @Test
    void testFlatMap() {
        AtomicBoolean end = new AtomicBoolean(false);
        List<Integer> result = new ArrayList<>();
        FlowSession session = new FlowSession();
        ProcessFlow<Integer> flow = Flows.<Integer>create(ThreadMode.SESSION).flatMap(input -> {
            Choir<Integer> publisher = Choir.create(emitter -> {
                IntStream.range(0, input).forEach(emitter::emit);
                emitter.complete();
            });
            TestBoundedEmitterDataBuilder<Integer> builder = new TestBoundedEmitterDataBuilder<>();
            FitBoundedEmitter<Integer, TestEmitterData<Integer>> emitter = new TestEmitter<>(new FlowSession(session),
                    publisher, builder);
            return Flows.source(emitter);
        }).close((sessionId, data) -> {
            result.add(data.getData());
        }, sessionId -> {
            FlowDebug.log("complete!");
            end.set(true);
        }, (sessionId, error) -> {});
        FlowDebug.log("session:" + session.getId());
        flow.offer(3, session);
        FlowSession flowSession = new FlowSession(session);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SESSION_COMPLETE, true);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SYSTEM, true);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.SESSION_TRACE_ID, UUIDUtil.uuid());
        flow.offer((Integer) null, flowSession);
        waitUntil(end::get, 1000);
        assertEquals(3, result.size());
        assertTrue(end.get());
    }

    @Test
    void testFlatMapReduce() {
        AtomicBoolean end = new AtomicBoolean(false);
        List<Integer> result = new ArrayList<>();
        FlowSession session = new FlowSession();
        ProcessFlow<Integer> flow = Flows.<Integer>create(ThreadMode.SESSION).flatMap(input -> {
            Choir<Integer> publisher = Choir.create(emitter -> {
                IntStream.range(0, input).forEach(emitter::emit);
                emitter.complete();
            });
            TestBoundedEmitterDataBuilder<Integer> builder = new TestBoundedEmitterDataBuilder<>();
            FitBoundedEmitter<Integer, TestEmitterData<Integer>> emitter = new TestEmitter<>(new FlowSession(session),
                    publisher, builder);
            return Flows.source(emitter);
        }).reduce(() -> 0, (acc, i) -> {
            FlowDebug.log("reduce: " + acc);
            return acc + i.getData();
        }).close((sessionId, data) -> {
            result.add(data);
        }, sessionId -> {
            FlowDebug.log("complete!");
            end.set(true);
        }, (sessionId, error) -> {});
        FlowDebug.log("session:" + session.getId());
        flow.offer(3, session);
        FlowSession flowSession = new FlowSession(session);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SESSION_COMPLETE, true);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SYSTEM, true);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.SESSION_TRACE_ID, UUIDUtil.uuid());
        flow.offer((Integer) null, flowSession);
        waitUntil(end::get, 10000);
        assertTrue(end.get());
        assertEquals(1, result.size());
    }

    @Test
    void testFlatMapConditionReduce() {
        AtomicBoolean end = new AtomicBoolean(false);
        List<Integer> result = new ArrayList<>();
        FlowSession session = new FlowSession();
        ProcessFlow<Integer> flow = Flows.<Integer>create(ThreadMode.SESSION)
                .map(i -> i + 1)
                .id("redo")
                .flatMap(input -> {
                    Choir<Integer> publisher = Choir.create(emitter -> {
                        IntStream.range(0, input).map(i -> i + input * 10).peek(i-> FlowDebug.log("i:" + i)).forEach(emitter::emit);
                        emitter.complete();
                    });
                    TestBoundedEmitterDataBuilder<Integer> builder = new TestBoundedEmitterDataBuilder<>();
                    FitBoundedEmitter<Integer, TestEmitterData<Integer>> emitter = new TestEmitter<>(
                            new FlowSession(session), publisher, builder);
                    return Flows.source(emitter);
                })
                .conditions()
                .matchTo(i -> i.getData() < 15, node -> node.reduce(() -> 0, (acc, i) -> {
                    FlowDebug.log("reduce: " + acc);
                    return acc + i.getData();
                }).map(i -> 2).to("redo"))
                .others(i -> i)
                .reduce(() -> 0, (acc, i) -> {
                    return acc + i.getData();
                })
                .close((sessionId, data) -> {
                    result.add(data);
                }, sessionId -> {
                    FlowDebug.log("complete!");
                    end.set(true);
                }, (sessionId, error) -> {});
        FlowDebug.log("session:" + session.getId());
        flow.offer(0, session);
        FlowSession flowSession = new FlowSession(session);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SESSION_COMPLETE, true);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SYSTEM, true);
        flowSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.SESSION_TRACE_ID, UUIDUtil.uuid());
        flow.offer((Integer) null, flowSession);
        waitUntil(end::get, 10000);
        assertTrue(end.get());
        assertEquals(1, result.size());
    }

    /**
     * 测试用的数据构建
     *
     * @param <D> 原始数据
     * @since 1.0
     */
    static class TestBoundedEmitterDataBuilder<D> implements Function<D, TestEmitterData<D>> {
        @Override
        public TestEmitterData<D> apply(D d) {
            return new TestEmitterData(d);
        }
    }

    /**
     * 用于测试的EmitterData
     *
     * @author x00576283
     * @since 1.0
     */
    @Data
    public static class TestEmitterData<T> {
        private T data;

        public TestEmitterData(T data) {
            this.data = data;
        }
    }
}