/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service.handlers;

import static com.huawei.fit.waterflow.common.Constant.FLOWS_EVENT_HANDLER_EXECUTOR;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.events.FlowCallbackEvent;
import com.huawei.fitframework.annotation.Asynchronous;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.log.Logger;

import java.util.List;

/**
 * 回调函数事件处理类
 *
 * @author 李哲峰
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
        FlowNode flowNode = flowDefinition.getFlowNode(flowContexts.get(0).getPosition());
        flowContexts.forEach(
                flowContext -> flowContext.getData().getContextData().put("nodeType", flowNode.getType().getCode()));

        eventData.getCallback().execute(flowContexts);

        log.info("[FlowCallbackEventHandler]: FlowCallbackEvent handling succeeded.");
    }
}
