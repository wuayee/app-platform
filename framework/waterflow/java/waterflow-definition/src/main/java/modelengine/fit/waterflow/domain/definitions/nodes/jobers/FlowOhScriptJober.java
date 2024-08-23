/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes.jobers;

import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.spi.WaterflowTaskHandler;

import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Map;

/**
 * 流程的ohScript任务
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class FlowOhScriptJober extends FlowJober {
    private static final Logger LOG = Logger.get(FlowOhScriptJober.class);

    private static final String FLOWABLE_HANDLE_TASK_GENERICABLE = "b735c87f5e7e408d852d8440d0b2ecdf";

    private static final String OHSCRIPT_FITABLE = "OhScript";

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) throws Throwable {
        List<Map<String, Object>> inputData = getInputs(inputs);

        List<Map<String, Object>> outputEntities = this.brokerClient.getRouter(WaterflowTaskHandler.class,
                        FLOWABLE_HANDLE_TASK_GENERICABLE)
                .route(new FitableIdFilter(OHSCRIPT_FITABLE))
                .communicationType(CommunicationType.ASYNC)
                .invoke(inputData);
        LOG.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, OHSCRIPT_FITABLE);
        return convertToFlowData(outputEntities, inputs.get(0));
    }
}
