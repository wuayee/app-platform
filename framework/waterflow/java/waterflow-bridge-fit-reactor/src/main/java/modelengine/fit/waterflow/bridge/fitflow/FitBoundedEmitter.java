/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.bridge.fitflow;

import static modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SESSION_COMPLETE;
import static modelengine.fit.waterflow.domain.stream.reactive.Publisher.IS_SYSTEM;
import static modelengine.fit.waterflow.domain.stream.reactive.Publisher.SESSION_TRACE_ID;

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
        publisher.subscribe(new FitBoundedEmitter.EmitterSubscriber<>(this));
    }

    @Override
    public synchronized void emit(D data, FlowSession session) {
        if (super.listeners.isEmpty()) {
            this.dataDuet.add(Tuple.duet(data, session));
            return;
        }
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

    private void doEmit(D data) {
        FlowSession newSession = new FlowSession(this.flowSession);
        this.emit(data, newSession);
    }

    private void doComplete() {
        FlowSession newSession = new FlowSession(this.flowSession);
        newSession.setInnerState(SESSION_TRACE_ID, UUIDUtil.uuid());
        newSession.setInnerState(IS_SYSTEM, true);
        newSession.setInnerState(IS_SESSION_COMPLETE, true);
        newSession.setInnerState(IS_BOUNDED, true);
        newSession.setInnerState(IS_BOUNDED_COMPLETE, true);
        this.emit(null, newSession);
    }

    private void doError(Exception cause) {
        FlowSession newSession = new FlowSession(this.flowSession);
        newSession.setInnerState(SESSION_TRACE_ID, UUIDUtil.uuid());
        newSession.setInnerState(IS_SYSTEM, true);
        newSession.setInnerState(IS_BOUNDED, true);
        newSession.setInnerState(IS_BOUNDED_ERROR, true);
        newSession.setInnerState(BOUNDED_ERROR, cause);
        this.emit(null, newSession);
    }

    @Override
    public void start(FlowSession session) {
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
            this.emitter.doEmit(target);
        }

        @Override
        public void complete() {
            this.emitter.isComplete = true;
            this.emitter.doComplete();
        }

        @Override
        public boolean isCompleted() {
            return this.emitter.isComplete;
        }

        @Override
        public void fail(Exception cause) {
            this.emitter.isError = true;
            this.emitter.doError(cause);
        }

        @Override
        public boolean isFailed() {
            return this.emitter.isError;
        }
    }
}
