/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowStoreJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowStoreJober.ServiceMeta;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberProperties;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * store的jober解析
 *
 * @author 宋永坦
 * @since 2024/5/8
 */
public class StoreJoberParser implements JoberParser {
    /**
     * 唯一名称的key
     */
    public static final String UNIQUE_NAME = "uniqueName";

    /**
     * 参数的key
     */
    public static final String PARAMS = "params";

    /**
     * 参数名称的key
     */
    public static final String NAME = "name";

    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowStoreJober flowJober = new FlowStoreJober();
        flowJober.setType(FlowJoberType.STORE_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        JSONObject entity = JSON.parseObject(flowJober.getProperties().get(FlowJoberProperties.ENTITY.getValue()));
        flowJober.setServiceMeta(this.getServiceMeta(entity));
        return flowJober;
    }

    private ServiceMeta getServiceMeta(Map<String, Object> config) {
        return ServiceMeta.builder()
                .uniqueName(cast(config.get(UNIQUE_NAME)))
                .params((ObjectUtils.<List<Map<String, Object>>>cast(config.get(PARAMS))).stream()
                        .map(param -> ObjectUtils.<String>cast(param.get(NAME)))
                        .collect(Collectors.toList()))
                .build();
    }
}
