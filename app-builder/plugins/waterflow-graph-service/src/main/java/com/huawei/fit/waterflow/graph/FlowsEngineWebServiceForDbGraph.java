/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.waterflow.graph;

import com.huawei.fit.jane.flow.graph.repo.FlowsGraphRepo;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.waterflow.biz.task.TagService;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowsService;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;

/**
 * 专为db的graph实现提供，整合后下线
 *
 * @author xiafei
 * @since 1.0
 */
@Component
public class FlowsEngineWebServiceForDbGraph extends FlowsEngineWebService {
    public FlowsEngineWebServiceForDbGraph(FlowContextsService flowContextsService,
                                           FlowsService flowsService,
                                           @Fit(alias = "DbFlowGraphRepo") FlowsGraphRepo flowsGraphRepo,
                                           Authenticator authenticator,
                                           TagService tagService,
                                           QueryFlowContextPersistRepo queryFlowContextPersistRepo) {
        super(flowContextsService, flowsService, flowsGraphRepo, authenticator, tagService,
                queryFlowContextPersistRepo);
    }
}
