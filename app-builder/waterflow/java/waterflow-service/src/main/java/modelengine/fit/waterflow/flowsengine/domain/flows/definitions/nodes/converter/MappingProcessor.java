/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

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
