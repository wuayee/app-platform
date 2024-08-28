/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package modelengine.fit.waterflow.graph;

import com.huawei.fit.jane.flow.graph.repo.FlowsGraphRepo;
import com.huawei.fit.jane.task.gateway.Authenticator;
import modelengine.fit.waterflow.biz.task.TagService;
import modelengine.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import modelengine.fit.waterflow.flowsengine.biz.service.FlowsService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
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
