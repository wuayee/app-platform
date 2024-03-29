/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.solo;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.flowable.Solo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示 {@link PublisherSoloAdapter} 的单元测试。
 *
 * @author 季聿阶 j00559309
 * @since 2024-02-11
 */
@DisplayName("测试 PublisherSoloAdapter")
public class PublisherSoloAdapterTest {
    @Test
    @DisplayName("订阅另一个 Solo 时，消费数据的数量等于另一个 Solo 内数据的数量")
    void shouldReturnAnotherSoloSize() {
        Solo<Integer> solo = Solo.fromPublisher(Solo.just(1));
        AtomicInteger count = new AtomicInteger();
        solo.subscribe((subscription, i) -> count.incrementAndGet());
        assertThat(count.get()).isEqualTo(1);
    }
}
