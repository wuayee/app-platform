/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.flow.Flows;
import com.huawei.fit.waterflow.domain.flow.ProcessFlow;
import com.huawei.fit.waterflow.domain.states.DataStart;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;

/**
 * 数据优先流的Start
 *
 * @param <O> 返回数据类型
 * @param <D> Flow的数据类型
 * @param <I> 入参数据类型
 * @since 1.0
 */
public class AiDataStart<O, D, I> {
    /**
     * 开始节点
     */
    protected AiDataStart<?, D, ?> start;

    final AiStart<O, D, I, ProcessFlow<D>, AiProcessFlow<D, ?>> state;

    private final D monoData;

    private final D[] fluxData;

    private final Emitter<D, FlowSession> emitter;

    public AiDataStart(AiStart<O, D, I, ProcessFlow<D>, AiProcessFlow<D, ?>> state, D data) {
        this(state, data, null, null);
    }

    public AiDataStart(AiStart<O, D, I, ProcessFlow<D>, AiProcessFlow<D, ?>> state, D[] data) {
        this(state, null, data, null);
    }

    public AiDataStart(AiStart<O, D, I, ProcessFlow<D>, AiProcessFlow<D, ?>> state, Emitter<D, FlowSession> emitter) {
        this(state, null, null, emitter);
    }

    private AiDataStart(AiStart<O, D, I, ProcessFlow<D>, AiProcessFlow<D, ?>> state, D monoData, D[] fluxData,
            Emitter<D, FlowSession> emitter) {
        this.state = state;
        this.monoData = monoData;
        this.fluxData = fluxData;
        this.emitter = emitter;
        this.start = this;
    }

    /**
     * 发射数据
     */
    protected void offer() {
        if (monoData != null) {
            this.start.state.getFlow().converse().offer(this.monoData);
            return;
        }
        if (fluxData != null) {
            this.start.state.getFlow().converse().offer(this.fluxData);
            return;
        }
        if (this.emitter != null) {
            this.start.state.getFlow().converse().offer(this.emitter);
        }
    }

    /**
     * 转换为 {@link DataStart}，用于AiDataStart到DataStart的解包装
     *
     * @return {@link DataStart}
     */
    protected DataStart<D, D, D> toDataStart() {
        if (monoData != null) {
            return Flows.mono(monoData);
        }
        if (fluxData != null) {
            return Flows.flux(fluxData);
        }
        if (this.emitter != null) {
            return Flows.source(emitter);
        }
        // 考虑抛异常
        return null;
    }
}
