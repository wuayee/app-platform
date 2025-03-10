/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.utils;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 流程执行信息工具类。
 *
 * @author 陈镕希
 * @since 2024-05-24
 */
public class FlowExecuteInfoUtil {
    private static final String EXECUTE_INFO_TYPE_KEY = "type";

    private static final String EXECUTE_INFO_INPUT_KEY = "input";

    private static final String EXECUTE_INFO_OUTPUT_KEY = "output";

    /**
     * 为flowData中ExecuteInfo添加新的InputMap
     *
     * @param flowData 流程实例运行时承载的业务数据的 {@link FlowData}。
     * @param newInputMap 节点对应type新的入参的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param nodeMetaId 节点唯一标识的 {@link String}。
     * @param executeInfoType 节点执行信息类型的 {@link String}。
     */
    public static void addInputMap2ExecuteInfoMap(FlowData flowData, Map<String, Object> newInputMap, String nodeMetaId,
            String executeInfoType) {
        addKeyValueToExecuteInfo(flowData, nodeMetaId, executeInfoType, EXECUTE_INFO_INPUT_KEY, newInputMap);
    }

    /**
     * 为flowData中ExecuteInfo添加新的OutputMap
     *
     * @param flowData 流程实例运行时承载的业务数据的 {@link FlowData}。
     * @param newOutputMap 节点对应type新的出参的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param nodeMetaId 节点唯一标识的 {@link String}。
     * @param executeInfoType 节点执行信息类型的 {@link String}。
     */
    public static void addOutputMap2ExecuteInfoMap(FlowData flowData, Map<String, Object> newOutputMap,
            String nodeMetaId, String executeInfoType) {
        addKeyValueToExecuteInfo(flowData, nodeMetaId, executeInfoType, EXECUTE_INFO_OUTPUT_KEY, newOutputMap);
    }

    private static void addKeyValueToExecuteInfo(FlowData flowData, String nodeMetaId, String executeInfoType,
            String key, Map<String, Object> value) {
        Map<String, Object> businessData = flowData.getBusinessData();
        Map<String, Object> internalMap = cast(businessData.getOrDefault(Constant.BUSINESS_DATA_INTERNAL_KEY, new HashMap<>()));
        Map<String, Object> executeInfoMap = cast(internalMap.getOrDefault(Constant.INTERNAL_EXECUTE_INFO_KEY, new HashMap<>()));
        List<Map<String, Object>> shapeExecuteInfoList = cast(
                executeInfoMap.getOrDefault(nodeMetaId, new ArrayList<>()));

        Optional<Map<String, Object>> optionalShapeSpecifyTypeExecuteInfo = shapeExecuteInfoList.stream()
                .filter(executeInfo -> executeInfoType.equals(executeInfo.get(EXECUTE_INFO_TYPE_KEY)))
                .findAny();
        if (optionalShapeSpecifyTypeExecuteInfo.isPresent()) {
            optionalShapeSpecifyTypeExecuteInfo.get().put(key, value);
        } else {
            // 某个shape的executeInfo被创建时，清空其他shape的executeInfo
            shapeExecuteInfoList.clear();
            Map<String, Object> newShapeExecuteInfo = new HashMap<>();
            newShapeExecuteInfo.put(EXECUTE_INFO_TYPE_KEY, executeInfoType);
            newShapeExecuteInfo.put(key, value);
            shapeExecuteInfoList.add(newShapeExecuteInfo);
        }

        executeInfoMap.put(nodeMetaId, shapeExecuteInfoList);
        internalMap.put(Constant.INTERNAL_EXECUTE_INFO_KEY, executeInfoMap);
        businessData.put(Constant.BUSINESS_DATA_INTERNAL_KEY, internalMap);
    }
}
