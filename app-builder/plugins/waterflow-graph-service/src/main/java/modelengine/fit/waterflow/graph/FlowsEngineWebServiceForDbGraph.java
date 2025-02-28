/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.waterflow.graph;

import modelengine.fit.jane.flow.graph.repo.FlowsGraphRepo;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.waterflow.biz.task.TagService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.service.FlowRuntimeService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;

/**
 * 专为db的graph实现提供，整合后下线
 *
 * @author xiafei
 * @since 1.0
 */
@Component
public class FlowsEngineWebServiceForDbGraph extends FlowsEngineWebService {
    public FlowsEngineWebServiceForDbGraph(FlowRuntimeService flowRuntimeService,
                                           FlowDefinitionService flowsService,
                                           @Fit(alias = "DbFlowGraphRepo") FlowsGraphRepo flowsGraphRepo,
                                           Authenticator authenticator,
                                           TagService tagService,
                                           QueryFlowContextPersistRepo queryFlowContextPersistRepo) {
        super(flowRuntimeService, flowsService, flowsGraphRepo, authenticator, tagService,
                queryFlowContextPersistRepo);
    }
}
