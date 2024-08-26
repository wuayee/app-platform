/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.events.FlowCallbackEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.events.FlowTaskCreatedEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 异步化节点间操作持久化版实现类
 *
 * @author 杨祥宇
 * @since 2023/9/18
 */
@Component
@Alias("flowContextPersistMessenger")
public class FlowContextPersistMessenger implements FlowContextMessenger {
    private static final Logger log = Logger.get(FlowContextPersistMessenger.class);

    private final Plugin plugin;

    public FlowContextPersistMessenger(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <I> void send(String nodeId, List<FlowContext<I>> contexts) {
        if (CollectionUtils.isEmpty(contexts)) {
            log.info("Empty contexts.");
            return;
        }
        log.info("Start sending an event.");
        this.plugin.runtime()
                .publisherOfEvents()
                .publishEvent(
                        new FlowTaskCreatedEvent(contexts.stream().map(IdGenerator::getId).collect(Collectors.toList()),
                                contexts.get(0).getStreamId(), nodeId, this));
    }

    @Override
    public <O> void sendCallback(FlowCallback callback, List<FlowContext<O>> contexts) {
        if (CollectionUtils.isEmpty(contexts)) {
            log.info("Empty contexts.");
            return;
        }
        log.info("Start sending a callback event.");
        this.plugin.runtime().publisherOfEvents().publishEvent(new FlowCallbackEvent(contexts, callback, this));
    }
}
