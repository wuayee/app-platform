/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.bridge.fitflow;

import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.model.Tuple;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 有限流的emitter
 *
 * @author x00576283
 * @since 2024/5/15
 */
public class FiniteEmitter<O, D extends FiniteEmitterData, T> implements Emitter<D, T> {
    private final List<EmitterListener<D, T>> listeners = new ArrayList<>();

    private final List<Tuple> dataDuet = new ArrayList<>();

    private final T token;

    /**
     * 构造方法
     *
     * @param publisher 流式的接口
     * @param builder 用于构建目标对象的builder
     */
    public FiniteEmitter(Publisher<O> publisher, FiniteEmitterDataBuilder<O, D> builder) {
        this(null, publisher, builder);
    }

    /**
     * 构造方法，增加了token传入
     *
     * @param token 一般是flowSession的token
     * @param publisher 流式的接口
     * @param builder 用于构建目标对象的builder
     */
    public FiniteEmitter(T token, Publisher<O> publisher, FiniteEmitterDataBuilder<O, D> builder) {
        this.token = token;
        publisher.subscribe(new EmitterSubscriber<>(this, builder));
    }

    /**
     * 错误
     *
     * @param cause 错误的异常
     */
    protected void onError(Exception cause) {
    }

    /**
     * 结束
     */
    protected void onComplete() {
    }

    /**
     * 扩展onConsume
     *
     * @param source 源数据
     * @param target 目标数据
     */
    protected void onConsume(O source, D target) {
    }

    private void doEmit(D data) {
        this.emit(data, this.token);
    }

    @Override
    public synchronized void emit(D data, T token) {
        if (this.listeners.isEmpty()) {
            this.dataDuet.add(Tuple.duet(data, token));
            return;
        }
        this.listeners.forEach(listener -> listener.handle(data, token));
    }

    @Override
    public synchronized void register(EmitterListener<D, T> listener) {
        this.listeners.add(listener);
        if (!this.dataDuet.isEmpty()) {
            this.dataDuet.forEach(duet ->
                    listener.handle(ObjectUtils.cast(duet.get(0).orElse(null)),
                            ObjectUtils.cast(duet.get(1).orElse(null))));
            this.dataDuet.clear();
        }
    }

    private static class EmitterSubscriber<O, D extends FiniteEmitterData, T> implements Subscriber<O> {
        private final FiniteEmitterDataBuilder<O, D> builder;

        private final FiniteEmitter<O, D, T> emitter;

        /**
         * 订阅
         *
         * @param emitter 数据发送
         * @param builder 数据构建
         */
        public EmitterSubscriber(FiniteEmitter<O, D, T> emitter, FiniteEmitterDataBuilder<O, D> builder) {
            this.emitter = emitter;
            this.builder = builder;
        }

        @Override
        public void onSubscribed(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void consume(O source) {
            D target = this.builder.data(source);
            this.emitter.onConsume(source, target);
            this.emitter.doEmit(target);
        }

        @Override
        public void complete() {
            this.emitter.onComplete();
            this.emitter.doEmit(this.builder.end());
        }

        @Override
        public boolean isCompleted() {
            return false;
        }

        @Override
        public void fail(Exception cause) {
            this.emitter.onError(cause);
            this.emitter.doEmit(this.builder.error(cause.getLocalizedMessage()));
        }

        @Override
        public boolean isFailed() {
            return false;
        }
    }
}
