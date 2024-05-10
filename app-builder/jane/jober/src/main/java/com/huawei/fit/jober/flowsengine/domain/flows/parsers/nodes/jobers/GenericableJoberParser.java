/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.jobers;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberProperties.ENTITY;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberType.GENERICABLE_JOBER;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowGenericableJober;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支持genericable调用的任务解析器
 *
 * @author s00558940
 * @since 2024/4/22
 */
public class GenericableJoberParser implements JoberParser {
    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowGenericableJober flowJober = new FlowGenericableJober();
        flowJober.setType(GENERICABLE_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        flowJober.setFitables(flowGraphData.getNodeJoberFitables(nodeIndex));
        flowJober.setGenericableConfig(
                getGenericableConfig(JSON.parseObject(flowJober.getProperties().get(ENTITY.getValue()))));
        flowJober.loadFitableId();
        return flowJober;
    }

    private FlowGenericableJober.GenericableConfig getGenericableConfig(Map<String, Object> config) {
        Map<String, Object> content = cast(config.get("genericable"));
        return FlowGenericableJober.GenericableConfig.builder()
                .id(cast(content.get("id")))
                .params(((List<Map<String, Object>>) content.get("params")).stream()
                        .map(param -> (String) param.get("name"))
                        .collect(Collectors.toList()))
                .build();
    }
}
