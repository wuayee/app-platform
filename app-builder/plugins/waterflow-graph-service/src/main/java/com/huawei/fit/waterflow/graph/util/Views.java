/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.graph.util;

import static com.huawei.fit.waterflow.biz.common.Constant.STREAM_ID_SEPARATOR;

import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.waterflow.biz.common.vo.FlowDefinitionVO;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 为视图提供工具方法。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public final class Views {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Views() {
    }

    private static void put(Map<String, Object> view, String key, Object value) {
        if (value != null) {
            view.put(key, value);
        }
    }

    /**
     * 将流引擎的数据和Elsa流的数据转换为视图数据。
     *
     * @param flowsEngine 流引擎数据
     * @param elsaFlow Elsa流数据
     * @return Map<String, Object> 视图数据
     */
    public static Map<String, Object> viewOf(Map<String, Object> flowsEngine, FlowInfo elsaFlow) {
        Map<String, Object> view = new LinkedHashMap<>(3);
        if (flowsEngine.isEmpty() && elsaFlow == null) {
            return view;
        }
        if (!flowsEngine.isEmpty()) {
            put(view, "metaId", flowsEngine.get("metaId"));
            put(view, "version", flowsEngine.get("version"));
        }
        if (elsaFlow != null) {
            put(view, "updateTime", new Date());
        }
        return view;
    }

    /**
     * 将流定义的数据转换为视图数据。
     *
     * @param flowDefinition 流定义
     * @return Map<String, Object> 视图数据
     */
    public static Map<String, Object> viewOfFlows(FlowDefinitionVO flowDefinition) {
        Map<String, Object> view = new LinkedHashMap<>(1);
        put(view, "flowDefinition", flowDefinition);
        return view;
    }

    /**
     * 将流定义的数据转换为视图数据。
     *
     * @param flows flows
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> viewOfFlows(Map<String, FlowDefinitionPO> flows) {
        List<Map<String, Object>> view = new ArrayList<>();
        flows.entrySet().stream().forEach(e -> {
            Map<String, Object> flowData = new LinkedHashMap<>();
            put(flowData, "releaseTime", e.getValue().getCreatedAt());
            put(flowData, "versionStatus", e.getValue().getStatus());
            put(flowData, "streamId", e.getValue().getMetaId() + STREAM_ID_SEPARATOR + e.getValue().getVersion());
            view.add(flowData);
        });
        return view;
    }

    /**
     * 将流定义的数据转换为视图数据。
     *
     * @param flowDefinition 流定义
     * @param graphData 图数据
     * @return Map<String, Object> 视图数据
     */
    public static Map<String, Object> viewOfFlows(FlowDefinition flowDefinition, String graphData) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        put(view, "flowDefinitionId", flowDefinition.getDefinitionId());
        put(view, "metaId", flowDefinition.getMetaId());
        put(view, "version", flowDefinition.getVersion());
        put(view, "graphData", graphData);
        return view;
    }
}
