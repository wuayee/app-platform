/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.breaker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.jade.aipp.code.config.CircuitBreakerConfig;
import modelengine.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Deque;

/**
 * 表示 {@link CircuitBreaker} 的测试用例。
 *
 * @author 邱晓霞
 * @since 2025-01-15
 */
public class CircuitBreakerTest {
    @Test
    @DisplayName("记录代码成功运行")
    public void shouldRecordResultWhenSuccess() {
        CircuitBreaker circuitBreaker = new CircuitBreaker(new CircuitBreakerConfig(50, 2, 2));
        circuitBreaker.recordResult(true);
        Deque<Boolean> slidingWindow = (Deque<Boolean>) ReflectionUtils.getField(circuitBreaker, "slidingWindow");
        assertEquals(1, slidingWindow.size());
        assertEquals(0, slidingWindow.stream().filter(s -> !s).count());
    }

    @Test
    @DisplayName("记录代码超时运行")
    public void shouldRecordResultWhenFailure() {
        CircuitBreaker circuitBreaker = new CircuitBreaker(new CircuitBreakerConfig(50, 2, 2));
        circuitBreaker.recordResult(false);
        Deque<Boolean> slidingWindow = (Deque<Boolean>) ReflectionUtils.getField(circuitBreaker, "slidingWindow");
        assertEquals(1, slidingWindow.size());
        assertEquals(1, slidingWindow.stream().filter(result -> !result).count());
    }

    @Test
    @DisplayName("熔断器关闭，允许执行代码")
    public void shouldReturnTrueWhenCircuitBreakerOpenClosed() {
        CircuitBreaker circuitBreaker = new CircuitBreaker(new CircuitBreakerConfig(50, 1, 1));
        assertDoesNotThrow(() -> circuitBreaker.acquirePermission());
    }

    @Test
    @DisplayName("熔断器打开，不允许执行代码")
    public void shouldReturnFalseWhenCircuitBreakerOpen() {
        CircuitBreaker circuitBreaker = new CircuitBreaker(new CircuitBreakerConfig(50, 1, 1));
        circuitBreaker.recordResult(false);
        assertEquals(CircuitBreaker.State.OPEN, ReflectionUtils.getField(circuitBreaker, "state"));
    }

    @Test
    @DisplayName("超过滑动窗口大小，移除前面的记录，加入新的记录")
    public void shouldPollWhenOutOfWindowSize() {
        CircuitBreaker circuitBreaker = new CircuitBreaker(new CircuitBreakerConfig(80, 1, 1));
        circuitBreaker.recordResult(true);
        assertEquals(CircuitBreaker.State.CLOSED, ReflectionUtils.getField(circuitBreaker, "state"));
        assertEquals(1, ((Deque<Boolean>) ReflectionUtils.getField(circuitBreaker, "slidingWindow")).size());
        circuitBreaker.recordResult(false);
        assertEquals(CircuitBreaker.State.OPEN, ReflectionUtils.getField(circuitBreaker, "state"));
        assertEquals(1, ((Deque<Boolean>) ReflectionUtils.getField(circuitBreaker, "slidingWindow")).size());
    }
}
