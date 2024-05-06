/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.flows;

import com.huawei.fit.waterflow.domain.flow.Flows;
import com.huawei.fit.waterflow.domain.flow.ProcessFlow;
import com.huawei.fit.waterflow.domain.states.Start;
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
}
