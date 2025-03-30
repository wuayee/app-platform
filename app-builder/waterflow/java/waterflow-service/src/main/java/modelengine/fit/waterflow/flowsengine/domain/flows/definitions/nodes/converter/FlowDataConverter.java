/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import java.util.Map;

/**
 * 节点间传递参数的转换器
 *
 * @author 宋永坦
 * @since 2024/4/17
 */
public interface FlowDataConverter {
    /**
     * 根据输入生成调用节点服务的入参。
     *
     * @param input 当前输入的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 转换后的参数的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    Map<String, Object> convertInput(Map<String, Object> input);

    /**
     * 根据输出生产调用节点服务的出参。
     *
     * @param result 节点输出的 {@link Object}。
     * @return 根据节点输出生成的调用节点服务出参 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    Map<String, Object> convertOutput(Object result);

    /**
     * 获取Converter中outputName
     *
     * @return outputName对应的 {@link String}。
     */
    String getOutputName();
}
