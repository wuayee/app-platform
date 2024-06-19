/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.core.Pattern;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.Collections;
import java.util.Map;

/**
 * 流程委托单元。
 *
 * @author 刘信宏
 * @since 2024-06-04
 */
public abstract class AbstractFlowPattern<I, O> implements FlowPattern<I, O> {
    private final LazyLoader<AiProcessFlow<I, O>> flowSupplier;
    private final Map<String, Object> args;

    protected AbstractFlowPattern() {
        this(null, null);
    }

    private AbstractFlowPattern(LazyLoader<AiProcessFlow<I, O>> flowSupplier, Map<String, Object> args) {
        this.flowSupplier = ObjectUtils.getIfNull(flowSupplier, () -> LazyLoader.of(this::buildFlow));
        this.args = ObjectUtils.getIfNull(args, Collections::emptyMap);
    }

    /**
     * 构造处理流程。
     *
     * @return 表示数据处理流程的 {@code <}{@link AiProcessFlow}{@code <}{@link I}{@code , }{@link O}{@code >}。
     */
    protected abstract AiProcessFlow<I, O> buildFlow();

    @Override
    public void register(EmitterListener<O, FlowSession> handler) {
        if (handler != null) {
            this.getFlow().register(handler);
        }
    }

    @Override
    public void emit(O data, FlowSession session) {
        FlowSession flowSession = new FlowSession(session);
        this.getFlow().emit(data, flowSession);
    }

    @Override
    public Pattern<I, O> bind(Map<String, Object> args) {
        // 使用同一个流程对象，否则 delegate 已经注册的流程会泄露，且新的流程没有注册时机。
        return new AbstractFlowPattern<I, O>(this.flowSupplier, args) {
            @Override
            protected AiProcessFlow<I, O> buildFlow() {
                // bind 产生的新对象使用的 {@code flowSupplier} 是初始对象的成员，
                // 其绑定的是初始对象的 buildFlow 方法，所以此处不会被调用。
                throw new IllegalStateException("Not allow to build flow again.");
            }
        };
    }

    @Override
    public O invoke(I data) {
        FlowSession session = getIfNull(cast(this.args.get(StateKey.FLOW_SESSION)), FlowSession::new);
        this.getFlow().converse(session).offer(data);
        return null;
    }

    /**
     * 获取同步委托单元。
     *
     * @return 表示同步委托单元的 {@link Pattern}{@code <}{@link I}{@code , }{@link O}{@code >}。
     * @throws IllegalStateException 当流程发生异常时。
     */
    public Pattern<I, O> sync() {
        return new SimplePattern<>((data, patternArgs) -> {
            FlowSession session = getIfNull(cast(patternArgs.get(StateKey.FLOW_SESSION)), FlowSession::new);
            return this.getFlow().converse(session).offer(data).await();
        });
    }

    /**
     * 获取被装饰的流程对象。
     *
     * @return 表示被装饰流程对象的 {@link Flow}{@code <}{@link I}{@code >}。
     */
    public Flow<I> origin() {
        return this.getFlow().origin();
    }

    private AiProcessFlow<I, O> getFlow() {
        return Validation.notNull(this.flowSupplier.get(), "The flow cannot be null.");
    }
}
