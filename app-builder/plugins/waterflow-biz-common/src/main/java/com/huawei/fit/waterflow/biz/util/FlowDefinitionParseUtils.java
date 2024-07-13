/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.util;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.shapehandler.DefaultShapeHandler;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.shapehandler.EventHandler;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.shapehandler.ShapeHandler;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 图graph解析成流程引擎所需格式
 *
 * @author y00679285
 * @since 2023/12/18
 */
public class FlowDefinitionParseUtils {
    /**
     * getParsedGraphData
     *
     * @param parsedData parsedData
     * @param version version
     * @return String
     */
    public static String getParsedGraphData(JSONObject parsedData, String version) {
        JSONObject result = new JSONObject();
        result.put("name", parsedData.get("title"));
        result.put("metaId", parsedData.get("id"));
        result.put("version", version);
        result.put("status", "active");
        result.put("exceptionFitables", parsedData.get("exceptionFitables"));
        JSONObject flowMeta = cast(parsedData.get("flowMeta"));
        if (!Objects.isNull(flowMeta)) {
            flowMeta.entrySet().stream().forEach(item -> result.put(item.getKey(), item.getValue()));
        }
        JSONArray pages = ObjectUtils.cast(parsedData.get("pages"));
        JSONArray shapes = ObjectUtils.cast(ObjectUtils.<JSONObject>cast(pages.get(0)).get("shapes"));
        List<Object> nodes = shapes.stream()
                .map(shape -> shapeToNode(ObjectUtils.cast(shape)))
                .collect(Collectors.toList());
        result.put("nodes", nodes);
        return result.toString();
    }

    private static Map<String, Object> shapeToNode(JSONObject shape) {
        String type = ObjectUtils.cast(shape.get("type"));
        ShapeHandler shapeHandler;
        if (type.toLowerCase(Locale.ROOT).endsWith("event")) {
            shapeHandler = new EventHandler(shape);
        } else {
            shapeHandler = new DefaultShapeHandler(shape);
        }
        Map<String, Object> node = shapeHandler.getDefaultConfig();
        node.putAll(shapeHandler.handleShape());
        return node;
    }
}
