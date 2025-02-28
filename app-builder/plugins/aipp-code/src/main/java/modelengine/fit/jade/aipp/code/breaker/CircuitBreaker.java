/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.breaker;

import static modelengine.fit.jade.aipp.code.code.CodeExecuteRetCode.CODE_EXECUTE_RESTRICTION;
import static modelengine.fit.jade.aipp.code.util.Constant.PERCENTAGE;

import modelengine.fit.jade.aipp.code.config.CircuitBreakerConfig;
import modelengine.jade.common.exception.ModelEngineException;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 熔断器。
 *
 * @author 邱晓霞
 * @since 2025-01-13
 */
public class CircuitBreaker {
    private State state = State.CLOSED;
    private final CircuitBreakerConfig circuitBreakerConfig;
    private final Deque<Boolean> slidingWindow;

    /**
     * 表示 {@link CircuitBreaker} 的构造方法。
     *
     * @param circuitBreakerConfig 表示熔断器配置的 {@link CircuitBreakerConfig}。
     */
    public CircuitBreaker(CircuitBreakerConfig circuitBreakerConfig) {
        this.circuitBreakerConfig = circuitBreakerConfig;
        this.slidingWindow = new ArrayDeque<>(circuitBreakerConfig.getWindowSize());
    }

    /**
     * 在调用前检查熔断器状态，决定是否允许执行。
     *
     * @throws ModelEngineException 当代码被限制执行时。
     **/
    public void acquirePermission() {
        if (this.state == State.OPEN) {
            throw new ModelEngineException(CODE_EXECUTE_RESTRICTION);
        }
    }

    /**
     * 在调用后记录结果。
     *
     * @param isSuccess 表示请求是否成功的 {@code boolean}。
     */
    public synchronized void recordResult(boolean isSuccess) {
        if (this.state == State.OPEN) {
            return;
        }

        if (this.slidingWindow.size() == this.circuitBreakerConfig.getWindowSize()) {
            this.slidingWindow.pollFirst();
        }
        this.slidingWindow.offerLast(isSuccess);

        if (this.slidingWindow.size() < this.circuitBreakerConfig.getMinimumNumberOfCalls()) {
            return;
        }

        long failureCount = this.slidingWindow.stream().filter(result -> !result).count();
        double failureRate = (failureCount / (double) this.slidingWindow.size()) * PERCENTAGE;
        if (failureRate >= this.circuitBreakerConfig.getFailureRateThreshold()) {
            this.state = State.OPEN;
        }
    }

    /**
     * 熔断器状态。
     */
    public enum State {
        /**
         * 允许代码执行。
         */
        CLOSED,

        /**
         * 限制代码执行。
         */
        OPEN
    }
}