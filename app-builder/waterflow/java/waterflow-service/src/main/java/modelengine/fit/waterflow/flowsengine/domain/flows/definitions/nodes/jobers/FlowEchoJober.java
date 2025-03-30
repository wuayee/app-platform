/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberProperties;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EchoJober实现类，输入什么回复什么
 *
 * @author 杨祥宇
 * @since 2023/8/15
 */
public class FlowEchoJober extends FlowJober {
    private static final String DEFAULT_ECHO_PREFIX = "echo: ";

    private static final String SKIP_VARIABLES_SPLITTER = ",";

    @Override
    protected Map<String, Object> modifyJoberConfig(FlowData flowData) {
        return new HashMap<>();
    }

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        String echoSign = this.properties.getOrDefault(FlowJoberProperties.ECHO_PREFIX.getValue(), DEFAULT_ECHO_PREFIX);
        String skipVariableRaw = this.properties.getOrDefault(FlowJoberProperties.SKIP_VARIABLES.getValue(),
                StringUtils.EMPTY);
        Set<String> skipVariables = new HashSet<>();
        skipVariables.add("_internal");
        if (StringUtils.isNotEmpty(skipVariableRaw)) {
            skipVariables.addAll(Arrays.asList(skipVariableRaw.split(SKIP_VARIABLES_SPLITTER)));
        }

        return inputs.stream().map(input -> {
            Map<String, Object> businessData = new HashMap<>(input.getBusinessData());
            input.getBusinessData()
                    .entrySet()
                    .stream()
                    .filter(entry -> !skipVariables.contains(entry.getKey()))
                    .forEach(entry -> businessData.put(entry.getKey(), echoSign + entry.getValue()));
            return FlowData.builder()
                    .operator(input.getOperator())
                    .startTime(input.getStartTime())
                    .contextData(input.getContextData())
                    .businessData(businessData)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    protected void restoreJoberConfig(Map<String, Object> oldValues) {

    }
}
