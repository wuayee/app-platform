/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.pattern.composite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

/**
 * {@link ConsumerComposite} 的单元测试。
 *
 * @author 季聿阶 j00559309
 * @since 2022-02-17
 */
@DisplayName("验证 ConsumerComposite")
public class ConsumerCompositeTest {
    @Nested
    @DisplayName("验证方法：createConcurrent()")
    class TestCreateConcurrent {
        @Test
        @DisplayName("当创建一个线程安全的消费者组合时，消费者组合不为 null")
        void returnNotNull() {
            Consumer<String> actual = ConsumerComposite.createConcurrent();
            assertThat(actual).isNotNull();
        }
    }
}
