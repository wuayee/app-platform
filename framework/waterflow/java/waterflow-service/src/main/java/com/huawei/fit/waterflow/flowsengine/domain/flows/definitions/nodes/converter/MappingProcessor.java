/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import java.util.Map;

/**
 * 对应映射对象的处理
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
public interface MappingProcessor {
    /**
     * 根据mappingConfig生成数据
     *
     * @param mappingConfig 映射配置
     * @param businessData 源数据
     * @return 生成的数据
     */
    Object generate(MappingNode mappingConfig, Map<String, Object> businessData);
}
