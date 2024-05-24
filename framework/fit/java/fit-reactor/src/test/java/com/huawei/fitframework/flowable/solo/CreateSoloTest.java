/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.solo;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

/**
 * 表示 {@link Solo#create(Consumer)} 的单元测试。
 *
 * @author 何天放 h00679269
 * @since 2024-05-22
 */
@DisplayName("测试通过 create 方法创建的 Solo")
public class CreateSoloTest {
    @Test
    @DisplayName("当多次发送数据以及正常终结信号时结果符合预期")
    void shouldCallConsumeAndComplete() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1, 1);
        Solo<Integer> solo = Solo.create(emitter -> {
            emitter.emit(1);
            emitter.complete();
        });
        solo.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
        assertThat(subscriber.receivedCompleted()).isTrue();
    }

    @Test
    @DisplayName("当多次发送超量数据时结果符合预期")
    void shouldOnlyCallConsumeOnceAndComplete() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1, 1);
        Solo<Integer> solo = Solo.create(emitter -> {
            emitter.emit(1);
            emitter.emit(2);
            emitter.emit(3);
            emitter.complete();
        });
        solo.subscribe(subscriber);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
        assertThat(subscriber.receivedCompleted()).isTrue();
    }

    @Test
    @DisplayName("当发送异常终结信号时结果符合预期")
    void shouldCallConsumeTwiceAndFail() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(2, 0);
        Solo<Integer> solo = Solo.create(emitter -> {
            emitter.fail(new Exception("Test message."));
        });
        solo.subscribe(subscriber);
        assertThat(subscriber.receivedFailed()).isTrue();
        assertThat(subscriber.getFailRecords().get(0).getData().getMessage()).isEqualTo("Test message.");
    }
}
