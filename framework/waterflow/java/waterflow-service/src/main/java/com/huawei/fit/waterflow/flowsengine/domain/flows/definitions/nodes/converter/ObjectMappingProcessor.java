/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象类型的数据生成，对应Map<String, Object>类型
 *
 * @author s00558940
 * @since 2024/4/19
 */
public class ObjectMappingProcessor extends AbstractMappingProcessor {
    private static final Logger LOG = Logger.get(ObjectMappingProcessor.class);

    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (!(mappingConfig.getValue() instanceof Map)) {
            LOG.error("The value can not be converted to object, name={}, value={}, valueType={}.",
                    mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
        }
        return mappingConfig.getValue();
    }

    @Override
    protected Object generateExpand(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (!(mappingConfig.getValue() instanceof List)) {
            LOG.error("The value can not be converted to object, name={}, value={}, valueType={}.",
                    mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
        }
        List<MappingNode> rawConfig = cast(mappingConfig.getValue());
        Map<String, Object> result = new HashMap<>();
        rawConfig.forEach(config -> result.put(config.getName(),
                MappingProcessorFactory.get(config).generate(config, businessData)));
        return result;
    }
}
