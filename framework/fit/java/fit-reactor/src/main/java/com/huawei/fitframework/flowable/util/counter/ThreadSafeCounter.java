/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.util.counter;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 表示 {@link Counter} 的线程安全版本实现。
 *
 * @author 何天放
 * @since 2024-02-20
 */
public class ThreadSafeCounter implements Counter {
    private static final Logger log = Logger.get(ThreadSafeCounter.class);

    private final AtomicLong value;
    private final List<CounterValueChangedObserver> observers = new ArrayList<CounterValueChangedObserver>();

    public ThreadSafeCounter(long initialValue) {
        this.value = new AtomicLong(initialValue);
    }

    @Override
    public long getValue() {
        return this.value.get();
    }

    @Override
    public void observeValueChanged(CounterValueChangedObserver observer) {
        if (observer == null) {
            return;
        }
        synchronized (this.observers) {
            this.observers.add(observer);
        }
    }

    @Override
    public void unobserveValueChanged(CounterValueChangedObserver observer) {
        if (observer == null) {
            return;
        }
        synchronized (this.observers) {
            this.observers.remove(observer);
        }
    }

    @Override
    public long increase() {
        return this.add(1);
    }

    @Override
    public long increase(long value) {
        Validation.greaterThan(value, 0, "The value of increase must be positive. [value={0}]", value);
        return this.add(value);
    }

    @Override
    public long decrease() {
        return this.add(-1);
    }

    @Override
    public long decrease(long value) {
        Validation.greaterThan(value, 0, "The value of decrease must be positive. [value={0}]", value);
        return this.add(-value);
    }

    private long add(long value) {
        long changed;
        while (true) {
            long from = this.value.get();
            long to = calculateTarget(value, from, from + value);
            if (this.value.compareAndSet(from, to)) {
                List<CounterValueChangedObserver> observersToNotify;
                synchronized (this.observers) {
                    observersToNotify = new ArrayList<>(this.observers);
                }
                for (CounterValueChangedObserver observer : observersToNotify) {
                    try {
                        observer.onValueChanged(this, from, to);
                    } catch (Exception cause) {
                        log.warn(StringUtils.format("Failed to observe value changed. [from={0}, to={1}]", from, to),
                                cause);
                    }
                }
                changed = Math.abs(to - from);
                break;
            }
        }
        return changed;
    }

    private static long calculateTarget(long changed, long from, long to) {
        if (changed > 0 && to < from) {
            // 防止上溢出环绕。
            return Long.MAX_VALUE;
        }
        if (changed < 0 && to > from) {
            // 防止下溢出环绕。
            return 0;
        }
        if (changed < 0 && to < 0) {
            // 向下截止于 0。
            return 0;
        }
        return to;
    }
}
