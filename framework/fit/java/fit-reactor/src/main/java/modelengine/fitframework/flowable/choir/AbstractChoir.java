/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.flowable.choir;

import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Solo;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.flowable.publisher.BufferPublisherDecorator;
import modelengine.fitframework.flowable.publisher.CountPublisherDecorator;
import modelengine.fitframework.flowable.publisher.FilterPublisherDecorator;
import modelengine.fitframework.flowable.publisher.FlatMapPublisherDecorator;
import modelengine.fitframework.flowable.publisher.MapPublisherDecorator;
import modelengine.fitframework.flowable.publisher.ReducePublisherDecorator;
import modelengine.fitframework.flowable.publisher.SkipPublisherDecorator;
import modelengine.fitframework.flowable.subscriber.BlockAllSubscriber;
import modelengine.fitframework.flowable.subscriber.FunctionalSubscriber;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 表示 {@link Choir} 的抽象实现类。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-07
 */
public abstract class AbstractChoir<T> implements Choir<T> {
    @Override
    public Choir<List<T>> buffer(int size) {
        return Choir.fromPublisher(new BufferPublisherDecorator<>(this, size));
    }

    @Override
    public Choir<T> distinct() {
        Set<T> elements = new HashSet<>();
        return this.filter(value -> {
            synchronized (elements) {
                return elements.add(value);
            }
        });
    }

    @Override
    public Solo<Long> count() {
        return Solo.fromPublisher(new CountPublisherDecorator<>(this));
    }

    @Override
    public Choir<T> filter(Predicate<T> filter) {
        return Choir.fromPublisher(new FilterPublisherDecorator<>(this, filter));
    }

    @Override
    public Solo<T> first() {
        return Solo.fromPublisher(this);
    }

    @Override
    public Solo<T> first(Predicate<T> filter) {
        return this.filter(filter).first();
    }

    @Override
    public <R> Choir<R> flatMap(Function<T, Publisher<R>> flatMapper) {
        return Choir.fromPublisher(new FlatMapPublisherDecorator<>(this, flatMapper));
    }

    @Override
    public <R> Choir<R> map(Function<T, R> mapper) {
        return Choir.fromPublisher(new MapPublisherDecorator<>(this, mapper));
    }

    @Override
    public Solo<T> reduce(BinaryOperator<T> reducer) {
        return Solo.fromPublisher(new ReducePublisherDecorator<>(this, reducer));
    }

    @Override
    public Choir<T> skip(int count) {
        return Choir.fromPublisher(new SkipPublisherDecorator<>(this, count));
    }

    @Override
    public void subscribe() {
        this.subscribe(Subscriber.empty());
    }

    @Override
    public void subscribe(BiConsumer<Subscription, T> consumeAction) {
        this.subscribe(null, consumeAction, null, null);
    }

    @Override
    public void subscribe(Consumer<Subscription> onSubscribedAction, BiConsumer<Subscription, T> consumeAction,
            Consumer<Subscription> completeAction, BiConsumer<Subscription, Exception> failAction) {
        this.subscribe(Subscriber.functional(
                ObjectUtils.nullIf(onSubscribedAction, FunctionalSubscriber.DEFAULT_ON_SUBSCRIBED_CHOIR_ACTION),
                ObjectUtils.nullIf(consumeAction, ObjectUtils.cast(FunctionalSubscriber.EMPTY_CONSUME_ACTION)),
                ObjectUtils.nullIf(completeAction, FunctionalSubscriber.EMPTY_COMPLETE_ACTION),
                ObjectUtils.nullIf(failAction, FunctionalSubscriber.EMPTY_FAIL_ACTION)));
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        this.subscribe0(ObjectUtils.getIfNull(subscriber, Subscriber::empty));
    }

    /**
     * 向发布者订阅以启动数据发送。
     *
     * @param subscriber 表示已订阅的 {@link Subscriber}{@code <}{@link T}{@code >}。
     */
    protected abstract void subscribe0(@Nonnull Subscriber<T> subscriber);

    @Override
    public List<T> blockAll() {
        BlockAllSubscriber<T> subscriber = new BlockAllSubscriber<>();
        this.subscribe(subscriber);
        return subscriber.getBlockedList();
    }
}
