/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.choir;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link EmitterChoir} 的单元测试。
 *
 * @author 季聿阶 j00559309
 * @since 2024-02-14
 */
@DisplayName("测试 EmitterChoir")
public class EmitterChoirTest {
    @Test
    @DisplayName("当发送者向多条响应式流同时发送数据时，结果符合预期")
    void shouldReturnMultipleCorrectReactiveFlow() {
        Emitter<Integer> emitter = Emitter.create();
        List<Integer> l1 = new ArrayList<>();
        Choir.fromEmitter(emitter).subscribe((subscription, i) -> l1.add(i));
        emitter.emit(1);
        List<Integer> l2 = new ArrayList<>();
        Choir.fromEmitter(emitter).subscribe((subscription, i) -> {
            l2.add(i);
        });
        emitter.emit(2);
        assertThat(l1).hasSize(2).contains(1, 2);
        assertThat(l2).hasSize(1).contains(2);
    }

    @Test
    @DisplayName("当发送者发送数据后取消时，结果符合预期")
    void shouldReturnCorrectReactiveFlowAfterCancelling() {
        Emitter<Integer> emitter = Emitter.create();
        emitter.emit(1);
        List<Integer> l2 = new ArrayList<>();
        Choir.fromEmitter(emitter).subscribe((subscription, i) -> {
            l2.add(i);
            subscription.cancel();
        });
        emitter.emit(2);
        emitter.emit(3);
        assertThat(l2).hasSize(1).contains(2);
    }

    @Test
    @DisplayName("当订阅者多次进行元素请求时，结果符合预期")
    void requestOperationShouldCalledTwiceWhenRequestTwice() {
        Subscriber<Integer> subscriber = new RecordSubscriber<>(2, 1);
        Emitter<Integer> emitter = Emitter.create();
        List<Long> requestRecords = new ArrayList<>();
        Choir.fromEmitter(emitter, requestRecords::add, null).subscribe(subscriber);
        emitter.emit(0);
        assertThat(requestRecords).hasSize(2).contains(2L, 1L);
    }

    @Test
    @DisplayName("当订阅者取消订阅关系时，结果符合预期")
    void cancelOperationShouldCalledWhenCancel() {
        Subscriber<Integer> subscriber = new RecordSubscriber<>(1, 0, 1);
        Emitter<Integer> emitter = Emitter.create();
        List<Long> cancelRecord = new ArrayList<>();
        Choir.fromEmitter(emitter, null, () -> {
            cancelRecord.add(0L);
        }).subscribe(subscriber);
        emitter.emit(0);
        assertThat(cancelRecord).hasSize(1).contains(0L);
    }
}
