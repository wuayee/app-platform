/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.bridge.fitflow;

import static com.huawei.fitframework.flowable.subscriber.FunctionalSubscriber.DEFAULT_ON_SUBSCRIBED_CHOIR_ACTION;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.subscriber.FunctionalSubscriber;
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
public class FiniteEmitter<O, D extends FiniteEmitterData, T> implements Emitter<D, FlowSession> {
    private final List<EmitterListener<D, FlowSession>> listeners = new ArrayList<>();

    private final List<Tuple> dataDuet = new ArrayList<>();

    @FunctionalInterface
    private interface Action {
        void exec();
    }

    public FiniteEmitter(Publisher<O> publisher, FiniteEmitterDataBuilder<O, D> builder) {
        FunctionalSubscriber<O> subscriber = Subscriber.functional(DEFAULT_ON_SUBSCRIBED_CHOIR_ACTION, (s, source) -> {
            D target = builder.data(source);
            FlowSession flowSession = new FlowSession();
            flowSession.setInnerState("consumer", (Action) () -> FiniteEmitter.this.onConsume(source, target));
            FiniteEmitter.this.emit(target, flowSession);
        }, s -> {
            FlowSession flowSession = new FlowSession();
            flowSession.setInnerState("consumer", (Action) FiniteEmitter.this::onComplete);
            FiniteEmitter.this.emit(builder.end(), flowSession);
        }, (s, cause) -> {
            FlowSession flowSession = new FlowSession();
            flowSession.setInnerState("consumer", (Action) () -> FiniteEmitter.this.onError(cause.getLocalizedMessage()));
            FiniteEmitter.this.emit(builder.error(cause.getLocalizedMessage()), flowSession);
        });
        publisher.subscribe(subscriber);
    }

    /**
     * 错误
     *
     * @param errorMessage 错误信息
     */
    private void onError(String errorMessage) {

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

    @Override
    public synchronized void emit(D data, FlowSession token) {
        if (this.listeners.isEmpty()) {
            this.dataDuet.add(Tuple.duet(data, token));
            return;
        }
        token.<Action>getInnerState("consumer").exec();
        this.listeners.forEach(listener -> listener.handle(data, token));
    }

    @Override
    public synchronized void register(EmitterListener<D, FlowSession> listener) {
        this.listeners.add(listener);
        if (!this.dataDuet.isEmpty()) {
            this.dataDuet.forEach(duet -> {
                // todo: 处理"consumer"不存在
                ((FlowSession)(duet.get(1).orElse(new FlowSession()))).<Action>getInnerState("consumer").exec();
                    listener.handle(ObjectUtils.cast(duet.get(0).orElse(null)),
                            ObjectUtils.cast(duet.get(1).orElse(null)));});
            this.dataDuet.clear();
        }
    }
}
