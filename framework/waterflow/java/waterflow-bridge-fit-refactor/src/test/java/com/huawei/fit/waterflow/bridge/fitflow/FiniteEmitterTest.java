/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.bridge.fitflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.flow.Flows;
import com.huawei.fit.waterflow.domain.utils.SleepUtil;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Publisher;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 测试类
 */
class FiniteEmitterTest {
    @Test
    void baseTest() {
        List<String> result = new ArrayList<>();
        Publisher<String> publisher = subscriber -> new Thread(() -> {
            subscriber.consume("1");
            subscriber.consume("2");
            subscriber.complete();
        }).start();
        FiniteEmitterDataBuilder<String, TestEmitterData<String>> builder = new TestFiniteEmitterDataBuilder<>();
        FiniteEmitter<String, TestEmitterData<String>, FlowSession> emitter = new FiniteEmitter<>(publisher, builder);
        emitter.register((data, token) -> {
            if (!data.isEnd()) {
                result.add(data.getData());
            }
        });
        SleepUtil.sleep(100);
        assertEquals(2, result.size());
    }

    @Test
    void testFlatMap() {
        List<Integer> result = new ArrayList<>();
        Flows.<Integer>create()
                .flatMap(input -> {
                    Choir<Integer> publisher = Choir.create(emitter -> {
                        IntStream.range(0, input).forEach(emitter::emit);
                        emitter.complete();
                    });
                    TestFiniteEmitterDataBuilder<Integer, Integer> builder =
                            new TestFiniteEmitterDataBuilder<>();
                    FiniteEmitter<Integer, TestEmitterData<Integer>, FlowSession> emitter =
                            new FiniteEmitter<>(publisher, builder);
                    return Flows.source(emitter);
                })
                .just(data -> {
                    if (!data.isEnd()) {
                        result.add(data.getData());
                    }
                })
                .close()
                .offer(3);
        SleepUtil.sleep(100);
        assertEquals(3, result.size());
    }
}