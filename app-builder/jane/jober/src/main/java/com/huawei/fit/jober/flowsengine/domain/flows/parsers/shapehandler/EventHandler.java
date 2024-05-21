/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.shapehandler;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * event解析
 *
 * @author 孙怡菲 s00664640
 * @since 2023-11-01
 */
public class EventHandler extends ShapeHandler {
    public EventHandler(JSONObject shape) {
        super(shape);
    }

    @Override
    public Map<String, Object> handleShape() {
        JSONObject shape = getShape();
        JSONObject meta = getMeta();
        Map<String, Object> result = new HashMap<>();
        result.put("to", shape.get("toShape"));
        result.put("from", shape.get("fromShape"));
        result.put("fromConnector", shape.get("definedFromConnector"));
        result.put("conditionRule", meta.get("conditionRule"));
        return result;
    }
}