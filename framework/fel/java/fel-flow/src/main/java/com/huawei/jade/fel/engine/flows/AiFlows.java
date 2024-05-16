/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.flows;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fit.waterflow.domain.flow.Flows;
import com.huawei.fit.waterflow.domain.flow.ProcessFlow;
import com.huawei.fit.waterflow.domain.states.Start;
import com.huawei.jade.fel.engine.activities.AiDataStart;
import com.huawei.jade.fel.engine.activities.AiStart;

/**
 * AI 流程创建入口的 API 集合。
 *
 * @author 刘信宏
 * @since 2024-04-29
 */
public class AiFlows {
    /**
     * 建立流程的头结点。
     *
     * @param <D> 表示流程的输入数据类型的 {@link D}。
     * @return 表示流程头结点的 {@link AiStart}{@code <}{@link D}{@code , }{@link D}{@code , }{@link D}{@code ,
     * }{@link ProcessFlow}{@code <}{@link D}{@code >, }{@link AiProcessFlow}{@code <}{@link D}{@code , ?>>}。
     */
    public static <D> AiStart<D, D, D, ProcessFlow<D>, AiProcessFlow<D, ?>> create() {
        Start<D, D, D, ProcessFlow<D>> start = Flows.create();
        AiProcessFlow<D, ?> flow = new AiProcessFlow<>(start.getFlow());
        return new AiStart<>(start, flow);
    }

    /**
     * 给定单个数据来构造一个AiDataStart，offer时发射该数据
     *
     * @param data 数据
     * @param <D> 数据类型
     * @return 以该数据为源的AiDataStart
     */
    public static <D> AiDataStart<D, D, D> mono(D data) {
        AiStart<D, D, D, ProcessFlow<D>, AiProcessFlow<D, ?>> start = AiFlows.create();
        return new AiDataStart<>(start, data);
    }

    /**
     * 给定的多条数据来构造一个AiDataStart，offer时发射多条数据
     *
     * @param data 多条数据
     * @param <D> 数据类型
     * @return 以多条数据为数据源的AiDataStart
     */
    public static <D> AiDataStart<D, D, D> flux(D... data) {
        AiStart<D, D, D, ProcessFlow<D>, AiProcessFlow<D, ?>> start = AiFlows.create();
        return new AiDataStart<>(start, data);
    }

    /**
     * 通过指定的 {@link Emitter}来构造一个AiDataStart，offer时将通过该Emitter来发射数据
     *
     * @param emitter 数据源
     * @param <D> 数据类型
     * @return 以Emitter为数据源的AiDataStart
     */
    public static <D> AiDataStart<D, D, D> source(Emitter<D, FlowSession> emitter) {
        AiStart<D, D, D, ProcessFlow<D>, AiProcessFlow<D, ?>> start = AiFlows.create();
        return new AiDataStart<>(start, emitter);
    }
}
