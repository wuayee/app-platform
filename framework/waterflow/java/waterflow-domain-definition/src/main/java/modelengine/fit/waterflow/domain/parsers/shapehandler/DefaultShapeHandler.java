/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.parsers.shapehandler;

import com.alibaba.fastjson.JSONObject;

import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 节点默认解析
 *
 * @author 孙怡菲
 * @since 1.0
 */
public class DefaultShapeHandler extends ShapeHandler {
    public DefaultShapeHandler(JSONObject shape) {
        super(shape);
    }

    @Override
    public Map<String, Object> handleShape() {
        JSONObject shape = getShape();
        Map<String, Object> result = new HashMap<>();
        String triggerMode = ObjectUtils.cast(shape.get("triggerMode"));
        result.put("triggerMode", triggerMode);
        // 适配auto节点配置task
        if (shape.containsKey("task")) {
            result.put("task", shape.get("task"));
            if (!Objects.isNull(shape.get("taskFilter")) && "state".equals(shape.get("type"))) {
                result.put("taskFilter", shape.get("taskFilter"));
            }
        }
        if (shape.containsKey("jober")) {
            result.put("jober", shape.get("jober"));
            if (!Objects.isNull(shape.get("joberFilter")) && "state".equals(shape.get("type"))) {
                result.put("joberFilter", shape.get("joberFilter"));
            }
        }
        if (shape.containsKey("callback")) {
            result.put("callback", shape.get("callback"));
        }
        return result;
    }
}
