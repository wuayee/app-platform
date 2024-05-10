/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter;

import java.util.Map;

/**
 * String类型的数据生成
 *
 * @author s00558940
 * @since 2024/4/18
 */
public class StringMappingProcessor extends AbstractMappingProcessor {
    @Override
    protected Object generateValue(MappingNode mappingConfig, Map<String, Object> businessData) {
        return mappingConfig.getValue().toString();
    }
}
