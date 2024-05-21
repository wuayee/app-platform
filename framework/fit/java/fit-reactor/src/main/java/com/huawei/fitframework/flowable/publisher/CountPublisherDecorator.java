/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.publisher;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.operation.AbstractOperation;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 表示 {@link Publisher} 的计算数据数量并将数量转换为一个响应式流的实现。
 *
 * @param <T> 表示发布者中数据类型的 {@link T}。
 * @author 黄夏露 h00804153
 * @since 2024-03-04
 */
public class CountPublisherDecorator<T> implements Publisher<Long> {
    private final Publisher<T> decorated;

    public CountPublisherDecorator(Publisher<T> decorated) {
        this.decorated = notNull(decorated, "The decorated count publisher cannot be null.");
    }

    @Override
    public void subscribe(Subscriber<Long> subscriber) {
        this.decorated.subscribe(new CountOperation<>(subscriber));
    }

    private static class CountOperation<T> extends AbstractOperation<T, Long> {
        private final AtomicLong count;

        CountOperation(Subscriber<Long> subscriber) {
            super(subscriber);
            this.count = new AtomicLong();
        }

        @Override
        protected void request0(long count) {
            super.request0(Long.MAX_VALUE);
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            this.count.getAndIncrement();
        }

        @Override
        protected void complete0(Subscription subscription) {
            this.getNextSubscriber().consume(this.count.get());
            super.complete0(subscription);
        }
    }
}
