/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;

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
