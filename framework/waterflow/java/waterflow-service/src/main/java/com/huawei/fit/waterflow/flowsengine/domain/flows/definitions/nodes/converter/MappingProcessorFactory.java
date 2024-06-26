/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fitframework.util.MapBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * 对应参数生成转换处理器的工厂
 *
 * @author s00558940
 * @since 2024/4/18
 */
public class MappingProcessorFactory {
    private static final Map<MappingNodeType, MappingProcessor> mappingProcessors
            = MapBuilder.<MappingNodeType, MappingProcessor>get()
            .put(MappingNodeType.BOOLEAN, new BooleanMappingProcessor())
            .put(MappingNodeType.INTEGER, new IntegerMappingProcessor())
            .put(MappingNodeType.NUMBER, new NumberMappingProcessor())
            .put(MappingNodeType.STRING, new StringMappingProcessor())
            .put(MappingNodeType.OBJECT, new ObjectMappingProcessor())
            .put(MappingNodeType.ARRAY, new ArrayMappingProcessor())
            .build();

    public static MappingProcessor get(MappingNode mappingConfig) {
        return Optional.ofNullable(mappingProcessors.get(mappingConfig.getType()))
                .orElseThrow(() -> new JobberParamException(ErrorCodes.INPUT_PARAM_IS_INVALID,
                        mappingConfig.getType().getCode()));
    }
}
