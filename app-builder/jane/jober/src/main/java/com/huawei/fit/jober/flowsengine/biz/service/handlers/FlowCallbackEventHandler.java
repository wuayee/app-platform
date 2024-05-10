/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service.handlers;

import static com.huawei.fit.jober.common.Constant.FLOWS_EVENT_HANDLER_EXECUTOR;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.events.FlowCallbackEvent;
import com.huawei.fitframework.annotation.Asynchronous;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.log.Logger;

import java.util.List;

/**
 * 回调函数事件处理类
 *
 * @author l00862071
 * @since 2023/12/12
 */
@Component
public class FlowCallbackEventHandler implements EventHandler<FlowCallbackEvent> {
    private static final Logger log = Logger.get(FlowCallbackEventHandler.class);

    private final FlowDefinitionRepo flowDefinitionRepo;

    public FlowCallbackEventHandler(FlowDefinitionRepo flowDefinitionRepo) {
        this.flowDefinitionRepo = flowDefinitionRepo;
    }

    /**
     * 处理流程回调事件
     *
     * @param eventData 表示流程回调事件
     */
    @Override
    @Asynchronous(executor = FLOWS_EVENT_HANDLER_EXECUTOR)
    public void handleEvent(FlowCallbackEvent eventData) {
        log.info("[FlowCallbackEventHandler]: Start to handle FlowCallbackEvent.");
        List<FlowContext<FlowData>> flowContexts = eventData.getFlowContexts();
        // 当回调函数执行时，所有FlowContext的streamId和nodeId的值都相同。
        FlowDefinition flowDefinition = flowDefinitionRepo.findByStreamId(flowContexts.get(0).getStreamId());
        FlowCallback flowCallback = flowDefinition.getFlowNode(flowContexts.get(0).getPosition()).getCallback();

        flowCallback.execute(flowContexts);

        log.info("[FlowCallbackEventHandler]: FlowCallbackEvent handling succeeded.");
    }
}
