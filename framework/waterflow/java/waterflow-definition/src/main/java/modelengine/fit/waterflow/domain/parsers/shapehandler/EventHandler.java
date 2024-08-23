/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers.shapehandler;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * event解析
 *
 * @author 孙怡菲
 * @since 1.0
 */
public class EventHandler extends ShapeHandler {
    public EventHandler(JSONObject shape) {
        super(shape);
    }

    @Override
    public Map<String, Object> handleShape() {
        JSONObject shape = getShape();
        Map<String, Object> result = new HashMap<>();
        result.put("to", shape.get("toShape"));
        result.put("from", shape.get("fromShape"));
        result.put("conditionRule", shape.get("conditionRule"));
        return result;
    }
}