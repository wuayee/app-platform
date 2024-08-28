/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes.jobers;

import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.spi.WaterflowTaskHandler;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Map;

/**
 * http类型的job调用
 *
 * @author 晏钰坤
 * @since 1.0
 */
public class FlowHttpJober extends FlowJober {
    private static final Logger LOG = Logger.get(FlowHttpJober.class);

    private static final String FLOWABLE_HANDLE_TASK_GENERICABLE = "b735c87f5e7e408d852d8440d0b2ecdf";

    private static final String HTTP_JOBER_FITABLE = "93f0b03c2ff94a46af5ace0088a8ce22";

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) throws Throwable {
        List<Map<String, Object>> contextData = getInputs(inputs);
        List<Map<String, Object>> outputs = this.brokerClient.getRouter(WaterflowTaskHandler.class,
                FLOWABLE_HANDLE_TASK_GENERICABLE).route(new FitableIdFilter(HTTP_JOBER_FITABLE)).invoke(contextData);
        LOG.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, HTTP_JOBER_FITABLE);
        return convertToFlowData(outputs, inputs.get(0));
    }
}

