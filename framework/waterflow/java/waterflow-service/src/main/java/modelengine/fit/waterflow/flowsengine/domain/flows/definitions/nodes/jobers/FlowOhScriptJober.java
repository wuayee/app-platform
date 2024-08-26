/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import com.huawei.fit.jober.FlowableService;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Map;

/**
 * ohScript的jober实现
 *
 * @author 杨祥宇
 * @since 2023/10/31
 */
public class FlowOhScriptJober extends FlowJober {
    private static final Logger log = Logger.get(FlowOhScriptJober.class);

    private static final String FLOWABLE_HANDLE_TASK_GENERICABLE = "b735c87f5e7e408d852d8440d0b2ecdf";

    private static final String OHSCRIPT_FITABLE = "OhScript";

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        List<Map<String, Object>> inputData = getInputs(inputs);

        List<Map<String, Object>> outputEntities = this.brokerClient.getRouter(FlowableService.class,
                        FLOWABLE_HANDLE_TASK_GENERICABLE)
                .route(new FitableIdFilter(OHSCRIPT_FITABLE))
                .communicationType(CommunicationType.ASYNC)
                .format(SerializationFormat.CBOR)
                .invoke(inputData);
        log.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, OHSCRIPT_FITABLE);
        return convertToFlowData(outputEntities, inputs.get(0));
    }
}
