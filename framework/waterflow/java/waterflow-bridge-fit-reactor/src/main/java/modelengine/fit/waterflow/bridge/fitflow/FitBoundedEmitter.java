/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package modelengine.fit.waterflow.bridge.fitflow;

import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.emitters.EmitterListener;
import modelengine.fit.waterflow.domain.emitters.FlowBoundedEmitter;
import modelengine.fit.waterflow.domain.utils.UUIDUtil;
import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 用于对接FIT的有限流的发射源。
 *
 * @author xiafei
 * @since 2024/8/16
 */
public abstract class FitBoundedEmitter<O, D> extends FlowBoundedEmitter<D> {
    private static final String CONSUMER_KEY = "consumer";

    private final List<Tuple> dataDuet = new ArrayList<>();

    private final FlowSession flowSession;

    private final Function<O, D> dataConverter;

    private boolean isComplete = false;

    private boolean isError = false;

    /**
     * 通过数据发布者和有限流数据构造器初始化 {@link FitBoundedEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
     *
     * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
     * @param dataConverter 表示用于数据类型转换的 {@link Function}{@code <}{@link O}{@code >}。
     */
    public FitBoundedEmitter(Publisher<O> publisher, Function<O, D> dataConverter) {
        this(new FlowSession(), publisher, dataConverter);
    }

    /**
     * 指定 {@link FlowSession} 构造 {@link FitBoundedEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
     *
     * @param flowSession 表示流程实例运行标识的 {@link FlowSession}。
     * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
     * @param dataConverter 表示用于数据类型转换的 {@link Function}{@code <}{@link O}{@code >}。
     */
    public FitBoundedEmitter(FlowSession flowSession, Publisher<O> publisher, Function<O, D> dataConverter) {
        this.flowSession = new FlowSession(flowSession);
        this.flowSession.setInnerState(BOUNDED_SESSION_ID, UUIDUtil.uuid());
        this.dataConverter = dataConverter;
        publisher.subscribe(new EmitterSubscriber<>(this));
    }

    @Override
    public synchronized void emit(D data, FlowSession session) {
        if (super.listeners.isEmpty()) {
            this.dataDuet.add(Tuple.duet(data, session));
            return;
        }
        session.<Action>getInnerState(CONSUMER_KEY).exec();
        super.emit(data, session);
    }

    @Override
    public synchronized void register(EmitterListener<D, FlowSession> listener) {
        super.listeners.add(listener);
        if (this.dataDuet.isEmpty()) {
            return;
        }
        this.dataDuet.forEach(duet -> {
            Optional<FlowSession> sessionOptional = duet.get(1);
            this.emit(ObjectUtils.cast(duet.get(0).orElse(null)), sessionOptional.orElse(null));
        });
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
     * @param target 标识转换后的 {@link D}。
     */
    protected abstract void consumeAction(O source, D target);

    private void doEmit(D data, Action action) {
        FlowSession newSession = new FlowSession(this.flowSession);
        newSession.setInnerState(CONSUMER_KEY, action);
        this.emit(data, newSession);
    }

    private void doComplete(Action action) {
        FlowSession newSession = new FlowSession(this.flowSession);
        newSession.setInnerState(CONSUMER_KEY, action);
        newSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.SESSION_TRACE_ID, UUIDUtil.uuid());
        newSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SYSTEM, true);
        newSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SESSION_COMPLETE, true);
        newSession.setInnerState(IS_BOUNDED, true);
        newSession.setInnerState(IS_BOUNDED_COMPLETE, true);
        this.emit(null, newSession);
    }

    private void doError(Exception cause, Action action) {
        FlowSession newSession = new FlowSession(this.flowSession);
        newSession.setInnerState(CONSUMER_KEY, action);
        newSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.SESSION_TRACE_ID, UUIDUtil.uuid());
        newSession.setInnerState(modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SYSTEM, true);
        newSession.setInnerState(IS_BOUNDED, true);
        newSession.setInnerState(IS_BOUNDED_ERROR, true);
        newSession.setInnerState(BOUNDED_ERROR, cause);
        this.emit(null, newSession);
    }

    @Override
    public void start(FlowSession session) {
    }

    @FunctionalInterface
    private interface Action {
        /**
         * 执行响应。
         */
        void exec();
    }

    /**
     * 订阅FIT响应式数据流的数据发射器
     *
     * @param <O> FIT响应式数据流中的数据类型
     * @param <D> 转换后的数据类型
     */
    private static class EmitterSubscriber<O, D> implements Subscriber<O> {
        private final FitBoundedEmitter<O, D> emitter;

        /**
         * 初始化订阅者。
         *
         * @param emitter 表示有限流的发射源的 {@link FitBoundedEmitter}{@code <}{@link O}{@code , }{@link D}{@code >}。
         */
        public EmitterSubscriber(FitBoundedEmitter<O, D> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onSubscribed(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void consume(O source) {
            D target = this.emitter.dataConverter.apply(source);
            this.emitter.doEmit(target, () -> this.emitter.consumeAction(source, target));
        }

        @Override
        public void complete() {
            this.emitter.isComplete = true;
            this.emitter.doComplete(this.emitter::completedAction);
        }

        @Override
        public boolean isCompleted() {
            return this.emitter.isComplete;
        }

        @Override
        public void fail(Exception cause) {
            this.emitter.isError = true;
            this.emitter.doError(cause, () -> this.emitter.errorAction(cause));
        }

        @Override
        public boolean isFailed() {
            return this.emitter.isError;
        }
    }
}
