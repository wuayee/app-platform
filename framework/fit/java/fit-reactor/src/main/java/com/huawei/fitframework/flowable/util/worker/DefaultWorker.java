/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.util.worker;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.FlowableException;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 表示响应式流辅助消费工具 {@link Worker} 的默认实现。
 *
 * @param <T> 表示响应式流中元素类型的 {@link T}。
 * @author 何天放 h00679269
 * @since 2024-02-20
 */
class DefaultWorker<T> implements Worker<T> {
    private final Publisher<T> publisher;
    private final WorkerObserver<T> observer;
    private final long id;
    private final AtomicBoolean completed = new AtomicBoolean();
    private final AtomicBoolean failed = new AtomicBoolean();

    private Subscription subscription;

    DefaultWorker(WorkerObserver<T> observer, Publisher<T> publisher, long id) {
        this.observer = notNull(observer, "The observer cannot be null.");
        this.publisher = notNull(publisher, "The publisher cannot be null.");
        this.id = id;
    }

    @Override
    public void onSubscribed(Subscription subscription) {
        isTrue(this.subscription == null, () -> new FlowableException("The subscriber cannot be subscribed twice."));
        this.subscription = subscription;
        this.observer.onWorkerSubscribed(subscription);
    }

    @Override
    public void consume(T data) {
        this.observer.onWorkerConsumed(data, this.id);
    }

    @Override
    public void complete() {
        this.completed.set(true);
        this.observer.onWorkerCompleted();
    }

    @Override
    public void fail(Exception cause) {
        this.failed.set(true);
        this.observer.onWorkerFailed(cause);
    }

    @Override
    public void run() {
        this.publisher.subscribe(this);
    }

    @Override
    public void request(long count) {
        notNull(this.subscription, "The previous subscription is not available.");
        this.subscription.request(count);
    }

    @Override
    public void cancel() {
        notNull(this.subscription, "The previous subscription is not available.");
        this.subscription.cancel();
    }

    @Override
    public boolean isCompleted() {
        return this.completed.get();
    }

    @Override
    public boolean isFailed() {
        return this.failed.get();
    }
}
