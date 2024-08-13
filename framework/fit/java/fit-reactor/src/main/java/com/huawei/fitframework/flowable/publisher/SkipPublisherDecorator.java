/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.publisher;

import static com.huawei.fitframework.inspection.Validation.greaterThan;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.operation.AbstractOperation;
import com.huawei.fitframework.flowable.util.counter.Counter;

/**
 * 表示 {@link Publisher} 的从开始跳过指定个数元素的过滤的实现。
 *
 * @param <T> 表示发布者中待过滤数据的类型的 {@link T}。
 * @author 鲁为
 * @since 2024-03-07
 */
public class SkipPublisherDecorator<T> implements Publisher<T> {
    private final Publisher<T> decorated;
    private final int count;

    public SkipPublisherDecorator(Publisher<T> decorated, int count) {
        this.decorated = notNull(decorated, "The decorated count publisher cannot be null.");
        this.count = greaterThan(count, 0, "The count to skip must be positive. [count={0}]", count);
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        this.decorated.subscribe(new SkipOperation<>(subscriber, this.count));
    }

    private static class SkipOperation<T> extends AbstractOperation<T, T> {
        private final Counter skipConsumeCount;

        SkipOperation(Subscriber<T> subscriber, int count) {
            super(subscriber);
            this.skipConsumeCount = Counter.create(count);
        }

        @Override
        protected void onSubscribed0(Subscription subscription) {
            long skipCount = this.skipConsumeCount.getValue();
            super.onSubscribed0(subscription);
            if (skipCount > 0) {
                super.request0(skipCount);
            }
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            if (hasSkipped() || !skip()) {
                this.getNextSubscriber().consume(data);
            }
        }

        private boolean hasSkipped() {
            return this.skipConsumeCount.getValue() == 0;
        }

        private boolean skip() {
            return this.skipConsumeCount.decrease() == 1;
        }
    }
}
