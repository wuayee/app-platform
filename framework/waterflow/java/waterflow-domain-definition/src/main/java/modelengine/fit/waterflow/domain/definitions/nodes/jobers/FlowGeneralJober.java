/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.definitions.nodes.jobers;

import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.spi.WaterflowTaskHandler;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.log.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * FlowNGeneralJober
 * 流程通用接口
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class FlowGeneralJober extends FlowJober {
    private static final Logger LOG = Logger.get(FlowGeneralJober.class);

    private static final String FLOWABLE_HANDLE_TASK_GENERICABLE = "b735c87f5e7e408d852d8440d0b2ecdf";

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) throws Throwable {
        List<Map<String, Object>> contextData = getInputs(inputs);

        List<Map<String, Object>> outputEntities = new ArrayList<>();
        for (String fitableId : fitables) {
            outputEntities = this.brokerClient.getRouter(WaterflowTaskHandler.class, FLOWABLE_HANDLE_TASK_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .timeout(1000, TimeUnit.MINUTES)
                    .invoke(contextData);
            LOG.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, fitableId);
        }
        return convertToFlowData(outputEntities, inputs.get(0));
    }
}
