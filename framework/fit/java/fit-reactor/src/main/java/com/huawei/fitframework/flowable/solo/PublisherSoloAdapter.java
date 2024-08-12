/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.solo;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.subscription.AbstractSubscription;
import com.huawei.fitframework.flowable.util.worker.Worker;
import com.huawei.fitframework.flowable.util.worker.WorkerObserver;
import com.huawei.fitframework.inspection.Nonnull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 表示 {@link Solo} 的指定 {@link Publisher} 的适配。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-09
 */
public class PublisherSoloAdapter<T> extends AbstractSolo<T> {
    private final Publisher<T> publisher;

    public PublisherSoloAdapter(Publisher<T> publisher) {
        this.publisher = notNull(publisher, "The publisher cannot be null.");
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        // 需要将原先的 Publisher 封装为一个新的 Subscription 从而达到控制元素发送数量和正常终结信号发送时机的目的。
        new PublisherAdapterSubscription<>(subscriber, this.publisher);
    }

    /**
     * 表示在将 {@link Publisher} 转换为 {@link Solo} 时辅助发送响应式流中元素的工具。
     * <p>能够限制向 {@link Subscriber} 发送元素的数量，并在恰当的时机发送正常终结信号。</p>
     *
     * @param <T> 表示响应式流中元素类型的 {@link T}。
     * @author 何天放
     * @since 2024-05-06
     */
    private static class PublisherAdapterSubscription<T> extends AbstractSubscription implements WorkerObserver<T> {
        private final Subscriber<T> subscriber;

        private final AtomicBoolean requested = new AtomicBoolean();
        private final AtomicBoolean failed = new AtomicBoolean();
        private final AtomicBoolean completed = new AtomicBoolean();
        private final Worker<T> worker;

        /**
         * 通过待转换的 {@link Publisher} 和其进行订阅的 {@link Subscriber} 创建辅助发送响应式流中元素的工具。
         *
         * @param subscriber 表示对于 {@link Publisher} 进行订阅的 {@link Subscriber}。
         * @param publisher 表示被 {@link Subscriber} 订阅的 {@link Publisher}。
         */
        public PublisherAdapterSubscription(Subscriber<T> subscriber, Publisher<T> publisher) {
            this.subscriber = notNull(subscriber, "The subscriber cannot be null.");
            this.worker = Worker.create(this, notNull(publisher, "The publisher cannot be null."), 0);
            worker.run();
        }

        @Override
        public void request0(long count) {
            if (this.requested.compareAndSet(false, true)) {
                this.worker.request(1);
            }
        }

        @Override
        public void cancel0() {
            this.worker.cancel();
        }

        @Override
        public void onWorkerSubscribed(Subscription subscription) {
            this.subscriber.onSubscribed(this);
        }

        @Override
        public void onWorkerConsumed(T data, long id) {
            this.subscriber.consume(data);
            if (this.completed.compareAndSet(false, true)) {
                this.subscriber.complete();
            }
        }

        @Override
        public void onWorkerFailed(Exception cause) {
            if (this.failed.compareAndSet(false, true)) {
                this.subscriber.fail(cause);
            }
        }

        @Override
        public void onWorkerCompleted() {
            if (this.completed.compareAndSet(false, true)) {
                this.subscriber.complete();
            }
        }
    }
}
