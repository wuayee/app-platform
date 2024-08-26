/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service.handlers;

import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.events.FlowCallbackEvent;
import modelengine.fitframework.annotation.Asynchronous;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.event.EventHandler;
import modelengine.fitframework.log.Logger;

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
    @Asynchronous(executor = Constant.FLOWS_EVENT_HANDLER_EXECUTOR)
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
