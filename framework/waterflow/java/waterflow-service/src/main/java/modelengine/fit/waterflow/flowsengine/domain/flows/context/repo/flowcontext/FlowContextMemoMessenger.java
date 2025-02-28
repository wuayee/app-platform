/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fitframework.log.Logger;

import java.util.List;

/**
 * 异步化节点间操作内存版实现类
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class FlowContextMemoMessenger implements FlowContextMessenger {
    private static final Logger log = Logger.get(FlowContextMemoMessenger.class);

    @Override
    public <I> void send(String nodeId, List<FlowContext<I>> contexts) {
        log.warn("FlowEngine memo messenger does not support sending events.");
    }

    @Override
    public <I> void sendCallback(FlowCallback callback, List<FlowContext<I>> contexts) {
        log.warn("FlowEngine memo messenger does not support sending events.");
    }
}
