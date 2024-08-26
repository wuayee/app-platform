/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers.shapehandler;

import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 节点解析
 *
 * @author 孙怡菲
 * @since 1.0
 */
@RequiredArgsConstructor
public abstract class ShapeHandler {
    private final JSONObject shape;

    /**
     * 获取默认配置
     *
     * @return 默认配置
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
     * 获取解析后的shape
     *
     * @return json类型的shape
     */
    public JSONObject getShape() {
        return shape;
    }

    /**
     * 解析shape
     *
     * @return 解析后的shape信息
     */
    public abstract Map<String, Object> handleShape();
}
