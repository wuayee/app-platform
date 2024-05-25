/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.bridge.fitflow;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.model.Tuple;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 有限流的发射源。
 *
 * @author 夏斐
 * @since 2024-05-15
 */
public abstract class FiniteEmitter<O, D extends FiniteEmitterData> implements Emitter<D, FlowSession> {
    private static final String CONSUMER_KEY = "consumer";

    private final List<EmitterListener<D, FlowSession>> listeners = new ArrayList<>();
    private final List<Tuple> dataDuet = new ArrayList<>();
    private final FlowSession flowSession;

    /**
     * 通过数据发布者和有限流数据构造器初始化 {@link FiniteEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
     *
     * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
     * @param builder 表示有限流数据构造器的 {@link FiniteEmitterDataBuilder}{@code <}{@link O}{@code , }{@link D}{@code >}。
     */
    public FiniteEmitter(Publisher<O> publisher, FiniteEmitterDataBuilder<O, D> builder) {
        this(null, publisher, builder);
    }

    /**
     * 指定 {@link FlowSession} 构造 {@link FiniteEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
     *
     * @param flowSession 表示流程实例运行标识的 {@link FlowSession}。
     * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
     * @param builder 表示有限流数据构造器的 {@link FiniteEmitterDataBuilder}{@code <}{@link O}{@code , }{@link D}{@code >}。
     */
    public FiniteEmitter(FlowSession flowSession, Publisher<O> publisher, FiniteEmitterDataBuilder<O, D> builder) {
        this.flowSession = flowSession;
        publisher.subscribe(new EmitterSubscriber<>(this, builder));
    }

    /**
     * 错误扩展响应。
     *
     * @param cause 表示错误异常的 {@link Exception}。
     */
    protected abstract void errorAction(Exception cause);

    /**
     * 结束扩展响应。
     */
    protected abstract void completedAction();

    /**
     * 发射数据时的扩展响应。
     *
     * @param source 表示源数据的 {@link O}。
     * @param target 表示目标数据的 {@link D}。
     */
    protected abstract void consumeAction(O source, D target);

    @Override
    public synchronized void emit(D data, FlowSession session) {
        if (this.listeners.isEmpty()) {
            this.dataDuet.add(Tuple.duet(data, session));
            return;
        }
        session.<Action>getInnerState(CONSUMER_KEY).exec();
        this.listeners.forEach(listener -> listener.handle(data, session));
    }

    @Override
    public synchronized void register(EmitterListener<D, FlowSession> listener) {
        this.listeners.add(listener);
        if (!this.dataDuet.isEmpty()) {
            this.dataDuet.forEach(duet -> {
                Optional<FlowSession> sessionOptional = duet.get(1);
                sessionOptional.ifPresent(session -> session.<Action>getInnerState(CONSUMER_KEY).exec());
                listener.handle(ObjectUtils.cast(duet.get(0).orElse(null)), sessionOptional.orElse(null));
            });
            this.dataDuet.clear();
        }
    }

    private void doEmit(D data, Action action) {
        FlowSession newSession = new FlowSession(this.flowSession);
        newSession.setInnerState(CONSUMER_KEY, action);
        this.emit(data, newSession);
    }

    @FunctionalInterface
    private interface Action {
        /**
         * 执行响应。
         */
        void exec();
    }

    private static class EmitterSubscriber<O, D extends FiniteEmitterData> implements Subscriber<O> {
        private final FiniteEmitterDataBuilder<O, D> builder;

        private final FiniteEmitter<O, D> emitter;

        /**
         * 初始化订阅者。
         *
         * @param emitter 表示有限流的发射源的 {@link FiniteEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
         * @param builder 表示有限流数据构造器的 {@link FiniteEmitterDataBuilder}{@code <}{@link O}{@code ,
         * }{@link D}{@code >}。
         */
        public EmitterSubscriber(FiniteEmitter<O, D> emitter, FiniteEmitterDataBuilder<O, D> builder) {
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
            this.emitter.doEmit(target, () -> this.emitter.consumeAction(source, target));
        }

        @Override
        public void complete() {
            this.emitter.doEmit(this.builder.end(), this.emitter::completedAction);
        }

        @Override
        public boolean isCompleted() {
            return false;
        }

        @Override
        public void fail(Exception cause) {
            this.emitter.doEmit(this.builder.error(cause.getLocalizedMessage()), () -> this.emitter.errorAction(cause));
        }

        @Override
        public boolean isFailed() {
            return false;
        }
    }
}
