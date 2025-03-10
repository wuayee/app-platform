/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link CircuitBreakerConfig} 的测试用例。
 *
 * @author 邱晓霞
 * @since 2025-01-16
 */
public class CircuitBreakerConfigTest {
    @Test
    @DisplayName("当失败率阈值参数校验不通过时，抛出异常")
    public void shouldThrowExceptionWhenInvalidFailureRateThreshold() {
        assertThrows(IllegalArgumentException.class, () -> new CircuitBreakerConfig(-1, 5, 10));
        assertThrows(IllegalArgumentException.class, () -> new CircuitBreakerConfig(101, 5, 10));
    }

    @Test
    @DisplayName("当开启熔断器前的最少运行次数参数校验不通过时，抛出异常")
    public void shouldThrowExceptionWhenInvalidMinimumNumberOfCalls() {
        assertThrows(IllegalArgumentException.class, () -> new CircuitBreakerConfig(50, 0, 10));
    }

    @Test
    @DisplayName("当滑动窗口大小参数校验不通过时，抛出异常")
    public void shouldThrowExceptionWhenInvalidWindowSize() {
        assertThrows(IllegalArgumentException.class, () -> new CircuitBreakerConfig(50, 5, 0));
    }
}
