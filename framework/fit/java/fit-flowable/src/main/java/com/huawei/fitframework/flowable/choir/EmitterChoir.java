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
import com.huawei.fitframework.util.ObjectUtils;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 表示 {@link Choir} 的数据发送器实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶 j00559309
 * @since 2024-02-14
 */
public class EmitterChoir<T> extends AbstractChoir<T> {
    private static final Consumer<Emitter<?>> EMPTY_EMITTER_CONSUMER = emitter -> {};
    private static final Consumer<Long> EMPTY_REQUEST_HANDLER = count -> {};
    private static final Runnable EMPTY_CANCEL_HANDLER = () -> {};

    private final Supplier<Emitter<T>> emitterSupplier;
    private final Consumer<Emitter<T>> emitterConsumer;
    private final Consumer<Long> requestHandler;
    private final Runnable cancelHandler;

    public EmitterChoir(Supplier<Emitter<T>> emitterSupplier, Consumer<Emitter<T>> emitterConsumer,
            Consumer<Long> requestHandler, Runnable cancelHandler) {
        this.emitterSupplier = notNull(emitterSupplier, "The emitter supplier cannot be null.");
        this.emitterConsumer = emitterConsumer == null ? ObjectUtils.cast(EMPTY_EMITTER_CONSUMER) : emitterConsumer;
        this.requestHandler = requestHandler == null ? EMPTY_REQUEST_HANDLER : requestHandler;
        this.cancelHandler = cancelHandler == null ? EMPTY_CANCEL_HANDLER : cancelHandler;
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        EmitterSubscription<T> subscription =
                new EmitterSubscription<>(subscriber, this.requestHandler, this.cancelHandler);
        subscriber.onSubscribed(subscription);
        Emitter<T> emitter = notNull(this.emitterSupplier.get(), "The result of emitter supplier cannot be null.");
        emitter.observe(subscription);
        this.emitterConsumer.accept(emitter);
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
