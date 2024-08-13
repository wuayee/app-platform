/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.publisher;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.operation.AbstractOperation;

import java.util.function.Function;

/**
 * 表示 {@link Publisher} 的携带数据类型转换的实现。
 *
 * @param <T> 表示发布者中数据转换前的类型的 {@link T}。
 * @param <R> 表示发布者中数据最终类型的 {@link R}。
 * @author 季聿阶
 * @since 2024-02-10
 */
public class MapPublisherDecorator<T, R> implements Publisher<R> {
    private final Publisher<T> decorated;
    private final Function<T, R> mapper;

    public MapPublisherDecorator(Publisher<T> decorated, Function<T, R> mapper) {
        this.decorated = notNull(decorated, "The decorated map publisher cannot be null.");
        this.mapper = notNull(mapper, "The mapper cannot be null.");
    }

    @Override
    public void subscribe(Subscriber<R> subscriber) {
        this.decorated.subscribe(new MapOperation<>(this.mapper, subscriber));
    }

    private static class MapOperation<T, R> extends AbstractOperation<T, R> {
        private final Function<T, R> mapper;

        /**
         * 使用数据映射方法和下游的订阅者来初始化 {@link MapOperation}。
         *
         * @param mapper 表示数据映射方法的 {@link Function}{@code <}{@link T}{@code , }{@link R}{@code >}。
         * @param subscriber 表示下游的订阅者的 {@link Subscriber}{@code <}{@link T}{@code >}。
         */
        MapOperation(Function<T, R> mapper, Subscriber<R> subscriber) {
            super(subscriber);
            this.mapper = mapper;
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            try {
                R result = this.mapper.apply(data);
                this.getNextSubscriber().consume(result);
            } catch (Exception cause) {
                this.getPreSubscription().cancel();
                this.getNextSubscriber().fail(cause);
            }
        }
    }
}
