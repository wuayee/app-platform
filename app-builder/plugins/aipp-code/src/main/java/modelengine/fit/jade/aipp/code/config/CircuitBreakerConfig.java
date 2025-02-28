/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.config;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.greaterThan;

import lombok.Getter;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;

/**
 * 熔断器配置类。
 *
 * @author 邱晓霞
 * @since 2025-01-16
 */
@Component
@Getter
public class CircuitBreakerConfig {
    private final double failureRateThreshold;
    private final int minimumNumberOfCalls;
    private final int windowSize;

    /**
     * 表示 {@link CircuitBreakerConfig} 的构造方法。
     *
     * @param failureRateThreshold 表示失败率阈值的 {@code double}。
     * @param minimumNumberOfCalls 表示开启熔断器前的最少运行次数 {@code int}。
     * @param windowSize 表示滑动窗口大小的 {@code int}。
     */
    public CircuitBreakerConfig(@Value("${code.breaker-manager.failure-rate-threshold}") double failureRateThreshold,
            @Value("${code.breaker-manager.minimum-number-of-calls}") int minimumNumberOfCalls,
            @Value("${code.breaker-manager.window-size}") int windowSize) {
        this.failureRateThreshold =
                between(failureRateThreshold, 0d, 100d, "The threshold of failure rate must be between 0 and 100.");
        this.minimumNumberOfCalls =
                greaterThan(minimumNumberOfCalls, 0, "The minimum number of calls must be positive.");
        this.windowSize = greaterThan(windowSize, 0, "The size of window must be positive.");
    }
}