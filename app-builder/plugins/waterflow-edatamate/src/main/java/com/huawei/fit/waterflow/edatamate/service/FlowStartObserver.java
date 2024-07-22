/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.QueryFlowDefinitionRepo;
import com.huawei.fit.waterflow.graph.FlowsEngineWebService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;

import java.util.Collections;

/**
 * 流程引擎启动后修正之前数据
 *
 * @author y00679285
 * @since 2023/12/30
 */
@Component
public class FlowStartObserver implements PluginStartedObserver {
    private static final Logger log = Logger.get(FlowStartObserver.class);

    private final QueryFlowDefinitionRepo flowDefinitionRepo;

    private final FlowContextPersistRepo flowContextPersistRepo;

    private final OrchestratorService orchestratorService;

    private final FlowsEngineWebService flowsEngineWebService;

    public FlowStartObserver(QueryFlowDefinitionRepo flowDefinitionRepo, FlowContextPersistRepo flowContextPersistRepo,
            OrchestratorService orchestratorService, FlowsEngineWebService flowsEngineWebService) {
        this.flowDefinitionRepo = flowDefinitionRepo;
        this.flowContextPersistRepo = flowContextPersistRepo;
        this.orchestratorService = orchestratorService;
        this.flowsEngineWebService = flowsEngineWebService;
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        if (!"jober".equals(plugin.metadata().name())) {
            return;
        }
        log.info("Plugin init, plugin name:{}", plugin.metadata().name());
        getAllFlowsInit();
        log.info("Process previous flow status success.");
    }

    private void getAllFlowsInit() {
        String createBy = "A3000";
        String tag = "data clean text";
        flowsEngineWebService.findFlowList(createBy, Collections.singletonList(tag), 0, 1,
                new com.huawei.fit.jane.common.entity.OperationContext());
    }
}
