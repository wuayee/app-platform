/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.flowable.choir;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示 {@link PublisherChoirAdapter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-02-09
 */
@DisplayName("测试 PublisherChoirAdapter")
public class PublisherChoirAdapterTest {
    @Test
    @DisplayName("订阅另一个 Choir 时，消费数据的数量等于另一个 Choir 内数据的数量")
    void shouldReturnAnotherChoirSize() {
        List<Integer> list = Arrays.asList(1, 2);
        Choir<Integer> choir = Choir.fromPublisher(Choir.fromIterable(list));
        AtomicInteger count = new AtomicInteger();
        choir.subscribe((subscription, i) -> count.incrementAndGet());
        assertThat(count.get()).isEqualTo(list.size());
    }
}
