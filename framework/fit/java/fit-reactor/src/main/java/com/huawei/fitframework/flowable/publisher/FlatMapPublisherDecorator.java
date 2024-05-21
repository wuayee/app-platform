/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.publisher;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.FlowableException;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.operation.AbstractOperation;
import com.huawei.fitframework.flowable.util.counter.Counter;
import com.huawei.fitframework.flowable.util.worker.Worker;
import com.huawei.fitframework.flowable.util.worker.WorkerObserver;

import java.util.function.Function;

/**
 * 表示 {@link Publisher} 的将每个数据通过指定的方式转换为一个响应式流并将各响应式流中的每个元素依次发送给下游的实现。
 *
 * @param <T> 表示发布者中数据转换前的类型的 {@link T}。
 * @param <R> 表示发布者中数据转换成的 {@link Publisher} 的元素类型的 {@link R}。
 * @author 何天放 h00679269
 * @since 2024-02-20
 */
public class FlatMapPublisherDecorator<T, R> implements Publisher<R> {
    private final Publisher<T> decorated;
    private final Function<T, Publisher<R>> flatMapper;

    public FlatMapPublisherDecorator(Publisher<T> decorated, Function<T, Publisher<R>> flatMapper) {
        this.decorated = notNull(decorated, "The decorated flat map publisher cannot be null.");
        this.flatMapper = notNull(flatMapper, "The flat mapper cannot be null.");
    }

    @Override
    public void subscribe(Subscriber<R> subscriber) {
        this.decorated.subscribe(new FlatMapOperation<>(subscriber, this.flatMapper));
    }

    private static class FlatMapOperation<T, R> extends AbstractOperation<T, R> implements WorkerObserver<R> {
        private final Function<T, Publisher<R>> flatMapper;
        private Worker<R> worker = null;

        private final Counter requested = Counter.create();

        FlatMapOperation(Subscriber<R> subscriber, Function<T, Publisher<R>> flatMapper) {
            super(subscriber);
            this.flatMapper = flatMapper;
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            if (this.worker != null && !this.worker.isCompleted()) {
                throw new FlowableException("The data in the current publisher is not completely consumed.");
            }
            Publisher<R> publisher;
            try {
                publisher = this.flatMapper.apply(data);
            } catch (Exception cause) {
                this.getPreSubscription().cancel();
                this.getNextSubscriber().fail(cause);
                return;
            }
            this.worker = Worker.create(this, publisher, Long.MAX_VALUE, this.requested.getValue());
            this.worker.run();
        }

        @Override
        protected void complete0(Subscription subscription) {
            if (this.worker != null && !this.worker.isCompleted()) {
                return;
            }
            super.complete0(subscription);
        }

        @Override
        protected void request0(long count) {
            this.requested.increase(count);
            if (this.worker == null || this.worker.isCompleted()) {
                this.getPreSubscription().request(1);
            } else {
                this.worker.request(count);
            }
        }

        @Override
        public void onWorkerConsumed(R data, long id) {
            this.requested.decrease();
            this.getNextSubscriber().consume(data);
        }

        @Override
        public void onWorkerFailed(Exception cause) {
            this.getNextSubscriber().fail(cause);
        }

        @Override
        public void onWorkerCompleted() {
            if (this.isCompleted()) {
                this.getNextSubscriber().complete();
                return;
            }
            if (this.requested.getValue() > 0) {
                this.getPreSubscription().request(1);
            }
        }
    }
}
