/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import java.util.Map;

/**
 * String类型的数据生成
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
public class StringMappingProcessor extends AbstractMappingProcessor {
    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        return mappingConfig.getValue().toString();
    }
}
