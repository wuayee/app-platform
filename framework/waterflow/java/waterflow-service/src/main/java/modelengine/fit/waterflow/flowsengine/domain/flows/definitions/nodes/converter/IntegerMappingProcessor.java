/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import modelengine.fitframework.log.Logger;

import java.util.Map;

/**
 * 整数类型的数据生成
 *
 * @author 宋永坦
 * @since 2024/4/19
 */
public class IntegerMappingProcessor extends AbstractMappingProcessor {
    private static final Logger LOG = Logger.get(IntegerMappingProcessor.class);

    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (mappingConfig.getValue() instanceof Integer) {
            return mappingConfig.getValue();
        }
        if (mappingConfig.getValue() instanceof String) {
            try {
                return Integer.valueOf((String) mappingConfig.getValue());
            } catch (NumberFormatException e) {
                LOG.error("The value can not be converted to integer.", e);
            }
        }
        LOG.error("The value can not be converted to integer, name={}, value={}, valueType={}.",
                mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());

        throw new JobberParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
    }
}
