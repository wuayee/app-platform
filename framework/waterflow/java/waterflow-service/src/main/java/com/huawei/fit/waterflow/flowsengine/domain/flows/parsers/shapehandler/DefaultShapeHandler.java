/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.shapehandler;

import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 节点默认解析
 *
 * @author 孙怡菲 s00664640
 * @since 2023-11-01
 */
public class DefaultShapeHandler extends ShapeHandler {
    public DefaultShapeHandler(JSONObject shape) {
        super(shape);
    }

    @Override
    public Map<String, Object> handleShape() {
        JSONObject shape = getShape();
        JSONObject meta = getMeta();
        Map<String, Object> result = new HashMap<>();
        String triggerMode = (String) meta.get("triggerMode");
        result.put("triggerMode", triggerMode);
        // 适配auto节点配置task
        if (meta.containsKey("task")) {
            result.put("task", meta.get("task"));
            if (!Objects.isNull(meta.get("taskFilter")) && shape.get("type").equals("state")) {
                result.put("taskFilter", meta.get("taskFilter"));
            }
        }
        if (meta.containsKey("jober")) {
            result.put("jober", meta.get("jober"));
            if (!Objects.isNull(meta.get("joberFilter")) && shape.get("type").equals("state")) {
                result.put("joberFilter", meta.get("joberFilter"));
            }
        }
        if (meta.containsKey("callback")) {
            result.put("callback", meta.get("callback"));
        }
        Set<String> internalKeys = new HashSet<>(
                Arrays.asList("type", "name", "id", "triggerMode", "task", "taskFilter", "jober", "joberFilter",
                        "callback"));
        // 只处理flowMeta中的属性，这里兼容逻辑如果meta和shape相同则表示老数据无需处理
        if (meta != shape) {
            meta.entrySet()
                    .stream()
                    .filter(item -> !internalKeys.contains(item.getKey()))
                    .forEach(item -> result.put(item.getKey(), item.getValue()));
        }
        return result;
    }
}
