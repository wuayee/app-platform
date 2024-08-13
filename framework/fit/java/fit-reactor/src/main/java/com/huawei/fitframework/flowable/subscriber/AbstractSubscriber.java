/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.subscriber;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.FlowableException;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.inspection.Nonnull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 表示 {@link Subscriber} 的抽象父类。
 *
 * @param <T> 表示订阅的数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-10
 */
public abstract class AbstractSubscriber<T> implements Subscriber<T> {
    private Subscription preSubscription;
    private final AtomicBoolean completed = new AtomicBoolean();
    private final AtomicBoolean failed = new AtomicBoolean();

    @Override
    public void onSubscribed(Subscription subscription) {
        isTrue(this.preSubscription == null, () -> new FlowableException("The subscriber cannot be subscribed twice."));
        this.preSubscription = notNull(subscription, "The subscription cannot be null.");
        this.onSubscribed0(this.preSubscription);
    }

    /**
     * 表示订阅关系发生时的事件。
     *
     * @param subscription 表示给定的订阅关系的 {@link Subscription}。
     */
    protected abstract void onSubscribed0(@Nonnull Subscription subscription);

    /**
     * 获取上游的订阅关系。
     *
     * @return 表示上游的订阅关系的 {@link Subscription}。
     */
    @Nonnull
    protected Subscription getPreSubscription() {
        return notNull(this.preSubscription,
                () -> new FlowableException("The previous subscription is not available before subscribed."));
    }

    @Override
    public void consume(T data) {
        if (this.completed.get() || this.failed.get()) {
            return;
        }
        this.consume(this.preSubscription, data);
    }

    /**
     * 消费订阅的数据。
     *
     * @param subscription 表示对上游的订阅关系的 {@link Subscription}。
     * @param data 表示待消费的数据的 {@link T}。
     */
    protected abstract void consume(@Nonnull Subscription subscription, T data);

    @Override
    public void complete() {
        if (this.failed.get() || !this.completed.compareAndSet(false, true)) {
            return;
        }
        this.complete(this.preSubscription);
    }

    /**
     * 表示正常终结信号。
     *
     * @param subscription 表示对上游的订阅关系的 {@link Subscription}。
     */
    protected abstract void complete(@Nonnull Subscription subscription);

    @Override
    public boolean isCompleted() {
        return this.completed.get();
    }

    @Override
    public void fail(Exception cause) {
        if (this.completed.get() || !this.failed.compareAndSet(false, true)) {
            return;
        }
        this.fail(this.preSubscription, cause);
    }

    /**
     * 表示异常终结信号。
     *
     * @param subscription 表示对上游的订阅关系的 {@link Subscription}。
     * @param cause 表示订阅中的错误信息的 {@link Exception}。
     */
    protected abstract void fail(@Nonnull Subscription subscription, Exception cause);

    @Override
    public boolean isFailed() {
        return this.failed.get();
    }
}
