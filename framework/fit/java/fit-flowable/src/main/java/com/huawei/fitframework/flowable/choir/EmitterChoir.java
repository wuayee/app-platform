/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.choir;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.subscription.AbstractSubscription;
import com.huawei.fitframework.inspection.Nonnull;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 表示 {@link Choir} 的数据发送器实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶 j00559309
 * @since 2024-02-14
 */
public class EmitterChoir<T> extends AbstractChoir<T> {
    private final Emitter<T> emitter;
    private final Consumer<Long> requestHandler;
    private final Runnable cancelHandler;

    public EmitterChoir(Emitter<T> emitter, Consumer<Long> requestHandler, Runnable cancelHandler) {
        this.emitter = notNull(emitter, "The emitter cannot be null.");
        this.requestHandler = requestHandler == null ? value -> {} : requestHandler;
        this.cancelHandler = cancelHandler == null ? () -> {} : cancelHandler;
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        EmitterSubscription<T> subscription =
                new EmitterSubscription<>(subscriber, this.requestHandler, this.cancelHandler);
        subscriber.onSubscribed(subscription);
        this.emitter.observe(subscription);
    }

    private static class EmitterSubscription<T> extends AbstractSubscription implements Emitter.Observer<T> {
        private final Subscriber<T> subscriber;
        private final AtomicLong requested = new AtomicLong();
        private final Consumer<Long> requestHandler;
        private final Runnable cancelHandler;

        public EmitterSubscription(Subscriber<T> subscriber, Consumer<Long> requestHandler, Runnable cancelHandler) {
            this.subscriber = notNull(subscriber, "The subscriber cannot be null.");
            this.requestHandler = notNull(requestHandler, "The request handler cannot be null.");
            this.cancelHandler = notNull(cancelHandler, "The cancel handler cannot be null.");
        }

        @Override
        protected void request0(long count) {
            this.requested.addAndGet(count);
            this.requestHandler.accept(count);
        }

        @Override
        protected void cancel0() {
            this.cancelHandler.run();
        }

        @Override
        public void onEmittedData(T data) {
            if (this.isCancelled()) {
                return;
            }
            if (requested.getAndDecrement() > 0) {
                this.subscriber.consume(data);
            } else {
                this.requested.getAndIncrement();
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
