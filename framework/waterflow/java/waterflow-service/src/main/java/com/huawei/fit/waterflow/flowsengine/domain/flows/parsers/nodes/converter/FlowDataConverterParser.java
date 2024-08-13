/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;

import java.util.Map;

/**
 * 流程任务数据转换器的解析器
 *
 * @author 宋永坦
 * @since 2024/4/17
 */
public interface FlowDataConverterParser {
    /**
     * 从配置中解析
     *
     * @param converterConfig 配置数据
     * @return 对应的转换器
     */
    FlowDataConverter parse(Map<String, Object> converterConfig);
}
