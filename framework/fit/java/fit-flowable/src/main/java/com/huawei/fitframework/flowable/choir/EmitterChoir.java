/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.choir;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.subscription.AbstractSubscription;
import com.huawei.fitframework.inspection.Validation;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 表示 {@link Choir} 的数据发送器实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶 j00559309
 * @since 2024-02-14
 */
public class EmitterChoir<T> extends AbstractChoir<T> {
    private final Emitter<T> emitter;

    public EmitterChoir(Emitter<T> emitter) {
        this.emitter = notNull(emitter, "The emitter cannot be null.");
    }

    @Override
    protected void subscribe0(Subscriber<T> subscriber) {
        EmitterSubscription<T> subscription = new EmitterSubscription<>(subscriber);
        subscriber.onSubscribed(subscription);
        this.emitter.observe(subscription);
    }

    private static class EmitterSubscription<T> extends AbstractSubscription implements Emitter.Observer<T> {
        private final Subscriber<T> subscriber;
        private final AtomicLong requested = new AtomicLong();

        public EmitterSubscription(Subscriber<T> subscriber) {
            this.subscriber = notNull(subscriber, "The subscriber cannot be null.");
        }

        @Override
        protected void request0(long count) {
            this.requested.addAndGet(count);
        }

        @Override
        public void onEmittedData(T data) {
            if (this.isCancelled()) {
                return;
            }
            if (requested.getAndDecrement() > 0) {
                this.subscriber.consume(data);
            } else {
                requested.getAndIncrement();
            }
        }

        @Override
        public void onCompleted() {
            if (this.isCancelled()) {
                return;
            }
            this.subscriber.complete();
        }

        @Override
        public void onFailed(Exception cause) {
            if (this.isCancelled()) {
                return;
            }
            this.subscriber.fail(cause);
        }
    }
}
