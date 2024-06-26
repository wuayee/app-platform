/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.shapehandler;

import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 节点解析
 *
 * @author 孙怡菲 s00664640
 * @since 2023-11-01
 */
@RequiredArgsConstructor
public abstract class ShapeHandler {
    private final JSONObject shape;

    /**
     * getDefaultConfig
     *
     * @return Map<String, Object>
     */
    public Map<String, Object> getDefaultConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("metaId", shape.get("id"));
        result.put("type", shape.get("type"));
        if (shape.get("text") instanceof String) {
            result.put("name", shape.get("text"));
        } else {
            String textHtml = ObjectUtils.cast(shape.get("textInnerHtml"));
            String regex = "<p[^>]*>(.*?)</p>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(textHtml);
            StringBuilder concatenatedText = new StringBuilder();
            while (matcher.find()) {
                String tagContent = matcher.group(1);
                concatenatedText.append(tagContent);
            }
            result.put("name", concatenatedText.toString());
        }
        return result;
    }

    /**
     * getShape
     *
     * @return shape
     */
    public JSONObject getShape() {
        return shape;
    }

    /**
     * handleShape
     *
     * @return Map<String, Object>
     */
    public abstract Map<String, Object> handleShape();

    protected JSONObject getMeta() {
        JSONObject flowMeta = this.shape.getJSONObject("flowMeta");
        if (Objects.isNull(flowMeta)) {
            // 兼容原来的逻辑
            return this.shape;
        }
        return flowMeta;
    }
}
