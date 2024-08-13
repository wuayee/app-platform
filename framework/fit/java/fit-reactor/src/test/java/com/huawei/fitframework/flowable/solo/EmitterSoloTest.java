/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.solo;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link Solo#fromEmitter(Emitter)} 的单元测试。
 *
 * @author 何天放
 * @since 2024-05-06
 */
@DisplayName("测试 EmitterSolo")
public class EmitterSoloTest {
    @Test
    @DisplayName("当发送者仅发送单个元素且未发送正常终结信号时，结果符合预期")
    void shouldReturnCompleteWhenOnlySendOne() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>();
        Emitter<Integer> emitter = Emitter.create();
        Solo.fromEmitter(emitter).subscribe(subscriber);
        emitter.emit(1);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
        assertThat(subscriber.receivedCompleted()).isTrue();
    }

    @Test
    @DisplayName("当发送者发送超量的数据时，结果符合预期")
    void shouldReturnOnlyOneElementWhenSendSuperfluous() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>();
        Emitter<Integer> emitter = Emitter.create();
        Solo.fromEmitter(emitter).subscribe(subscriber);
        emitter.emit(1);
        emitter.emit(2);
        assertThat(subscriber.getElements()).hasSize(1).contains(1);
    }
}
