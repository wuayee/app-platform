/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service.handlers;

import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.events.FlowCallbackEvent;
import modelengine.fitframework.annotation.Asynchronous;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.event.EventHandler;
import modelengine.fitframework.log.Logger;

/**
 * 回调函数事件处理类
 *
 * @author 李哲峰
 * @since 2023/12/12
 */
@Component
public class FlowCallbackEventHandler implements EventHandler<FlowCallbackEvent> {
    private static final Logger log = Logger.get(FlowCallbackEventHandler.class);

    /**
     * 初始化对象
     */
    public FlowCallbackEventHandler() {
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
        eventData.getCallback().execute(eventData.getFlowContexts());
        log.info("[FlowCallbackEventHandler]: FlowCallbackEvent handling succeeded.");
    }
}
