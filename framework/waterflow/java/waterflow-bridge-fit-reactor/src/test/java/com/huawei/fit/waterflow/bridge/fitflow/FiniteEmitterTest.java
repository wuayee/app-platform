/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.bridge.fitflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fit.waterflow.domain.flow.Flows;
import com.huawei.fit.waterflow.domain.utils.SleepUtil;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Publisher;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * 测试类
 */
class FiniteEmitterTest {
    private static void waitUntil(Supplier<Boolean> stop, int most) {
        int time = 0;
        int step = 10;
        while (!stop.get() && time < most) {
            SleepUtil.sleep(step);
            time += step;
        }
    }

    private static class TestFiniteEmitter<O, D extends FiniteEmitterData> extends FiniteEmitter<O, D> {
        private final AtomicInteger count = new AtomicInteger(0);

        /**
         * 通过数据发布者和有限流数据构造器初始化 {@link TestFiniteEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
         *
         * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
         * @param builder 表示有限流数据构造器的 {@link FiniteEmitterDataBuilder}{@code <}{@link O}{@code ,
         * }{@link D}{@code >}。
         */
        public TestFiniteEmitter(Publisher<O> publisher, FiniteEmitterDataBuilder<O, D> builder) {
            super(publisher, builder);
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
        FiniteEmitterDataBuilder<String, TestEmitterData<String>> builder = new TestFiniteEmitterDataBuilder<>();
        FiniteEmitter<String, TestEmitterData<String>> emitter = new TestFiniteEmitter<>(publisher, builder);
        emitter.register((data, token) -> {
            if (!data.isEnd()) {
                result.add(data.getData());
            } else {
                end.set(true);
            }
        });
        waitUntil(end::get, 1000);
        assertEquals(2, result.size());
    }

    @Test
    void testFlatMap() {
        AtomicBoolean end = new AtomicBoolean(false);
        List<Integer> result = new ArrayList<>();
        Flows.<Integer>create()
                .flatMap(input -> {
                    Choir<Integer> publisher = Choir.create(emitter -> {
                        IntStream.range(0, input).forEach(emitter::emit);
                        emitter.complete();
                    });
                    TestFiniteEmitterDataBuilder<Integer, Integer> builder =
                            new TestFiniteEmitterDataBuilder<>();
                    FiniteEmitter<Integer, TestEmitterData<Integer>> emitter =
                            new TestFiniteEmitter<>(publisher, builder);
                    return Flows.source(emitter);
                })
                .just(data -> {
                    if (!data.isEnd()) {
                        result.add(data.getData());
                    } else {
                        end.set(true);
                    }
                })
                .close()
                .offer(3);
        waitUntil(end::get, 500);
        assertEquals(3, result.size());
    }
}