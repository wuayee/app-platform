/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.choir;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.subscription.AbstractSubscription;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.LockUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

/**
 * 表示 {@link Choir} 的迭代器实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-07
 */
public class IterableChoir<T> extends AbstractChoir<T> {
    private final Iterable<T> iterable;

    public IterableChoir(Iterable<T> iterable) {
        this.iterable = getIfNull(iterable, Collections::emptyList);
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        subscriber.onSubscribed(new IterableSubscription<>(subscriber, this.iterable.iterator()));
    }

    private static class IterableSubscription<T> extends AbstractSubscription {
        private final Subscriber<T> subscriber;
        private final Iterator<T> iterator;
        private final AtomicBoolean completed = new AtomicBoolean();
        private final AtomicBoolean running = new AtomicBoolean();
        private final AtomicLong counter = new AtomicLong();
        private final Lock lock = LockUtils.newReentrantLock();

        IterableSubscription(Subscriber<T> subscriber, Iterator<T> iterator) {
            this.subscriber = subscriber;
            this.iterator = iterator;
        }

        @Override
        protected void request0(long count) {
            if (this.completed.get()) {
                return;
            }
            this.lock.lock();
            try {
                long pre = this.counter.getAndAdd(count);
                this.onCounterValueChanged(pre);
            } finally {
                this.lock.unlock();
            }
        }

        private void onCounterValueChanged(long pre) {
            if (pre > 0 || this.isCancelled() || this.completed.get() || !this.running.compareAndSet(false, true)) {
                return;
            }
            while (true) {
                if (this.isCancelled() || this.completed.get()) {
                    this.running.compareAndSet(true, false);
                    return;
                }
                this.lock.lock();
                try {
                    if (this.counter.get() <= 0) {
                        this.running.compareAndSet(true, false);
                        break;
                    }
                } finally {
                    this.lock.unlock();
                }
                this.counter.decrementAndGet();
                if (this.iterator.hasNext()) {
                    T data = this.iterator.next();
                    try {
                        this.subscriber.consume(data);
                    } catch (Exception e) {
                        this.subscriber.fail(e);
                    }
                } else {
                    this.running.compareAndSet(true, false);
                    break;
                }
            }
            if (!this.iterator.hasNext() && !this.isCancelled() && this.completed.compareAndSet(false, true)) {
                this.subscriber.complete();
            }
        }
    }
}
