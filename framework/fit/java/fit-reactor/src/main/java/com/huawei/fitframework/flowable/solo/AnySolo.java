/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.solo;

import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.subscription.AbstractSubscription;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.LockUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 表示 {@link Solo} 的单数据实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-11
 */
public class AnySolo<T> extends AbstractSolo<T> {
    private final T data;

    public AnySolo(T data) {
        this.data = data;
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        subscriber.onSubscribed(new AnySubscription<>(subscriber, this.data));
    }

    private static class AnySubscription<T> extends AbstractSubscription {
        private final Subscriber<T> subscriber;
        private final T data;

        private final AtomicLong counter = new AtomicLong();
        private final AtomicBoolean consumed = new AtomicBoolean();
        private final Object lock = LockUtils.newSynchronizedLock();

        AnySubscription(Subscriber<T> subscriber, T data) {
            this.subscriber = subscriber;
            this.data = data;
        }

        @Override
        protected void request0(long count) {
            if (this.consumed.get()) {
                return;
            }
            synchronized (this.lock) {
                long pre = this.counter.getAndAdd(count);
                this.onCounterValueChanged(pre);
            }
        }

        private void onCounterValueChanged(long pre) {
            if (pre > 0) {
                return;
            }
            if (this.isCancelled() || !this.consumed.compareAndSet(false, true)) {
                return;
            }
            this.subscriber.consume(this.data);
            if (!this.isCancelled()) {
                this.subscriber.complete();
            }
        }
    }
}
