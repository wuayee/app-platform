/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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
 * 专为elsa的graph实现，整合后下线
 *
 * @author xiafei
 * @since 1.0
 */
@Component
public class FlowsEngineWebServiceForElsaGraph extends FlowsEngineWebService {
    public FlowsEngineWebServiceForElsaGraph(FlowContextsService flowContextsService,
                                             FlowsService flowsService,
                                             @Fit(alias = "ElsaFlowGraphRepo") FlowsGraphRepo flowsGraphRepo,
                                             Authenticator authenticator,
                                             TagService tagService,
                                             QueryFlowContextPersistRepo queryFlowContextPersistRepo) {
        super(flowContextsService, flowsService, flowsGraphRepo, authenticator, tagService,
                queryFlowContextPersistRepo);
    }
}
