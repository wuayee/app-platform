/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers;

import static modelengine.fitframework.util.ObjectUtils.cast;

import com.alibaba.fastjson.JSON;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowGenericableJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberProperties;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支持genericable调用的任务解析器
 *
 * @author 宋永坦
 * @since 2024/4/22
 */
public class GenericableJoberParser implements JoberParser {
    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowGenericableJober flowJober = new FlowGenericableJober();
        flowJober.setType(FlowJoberType.GENERICABLE_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        flowJober.setFitables(flowGraphData.getNodeJoberFitables(nodeIndex));
        flowJober.setGenericableConfig(getGenericableConfig(
                JSON.parseObject(flowJober.getProperties().get(FlowJoberProperties.ENTITY.getValue()))));
        flowJober.loadFitableId();
        return flowJober;
    }

    private FlowGenericableJober.GenericableConfig getGenericableConfig(Map<String, Object> config) {
        Map<String, Object> content = cast(config.get("genericable"));
        return FlowGenericableJober.GenericableConfig.builder()
                .id(cast(content.get("id")))
                .params((ObjectUtils.<List<Map<String, Object>>>cast(content.get("params"))).stream()
                        .map(param -> ObjectUtils.<String>cast(param.get("name")))
                        .collect(Collectors.toList()))
                .build();
    }
}
