/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.Attributes;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.EventAttributes;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.NodeAttributes;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * nodes 提取器.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class NodesExtractor implements ValueExtractor {
    @Override
    public Object extract(AttributesData attributesData) {
        JSONArray pages = ObjectUtils.cast(attributesData.getData().get("pages"));
        JSONArray shapes = ObjectUtils.cast(ObjectUtils.<JSONObject>cast(pages.get(0)).get("shapes"));
        return shapes.stream()
                .map(s -> this.toShape(ObjectUtils.cast(s)))
                .filter(Attributes::isRunnable)
                .map(Attributes::getData)
                .collect(Collectors.toList());
    }

    private Attributes toShape(JSONObject data) {
        String type = ObjectUtils.cast(data.get("type"));
        AttributesData attributesData = new AttributesData(data);
        return type.toLowerCase(Locale.ROOT).endsWith("event")
                ? new EventAttributes(attributesData)
                : new NodeAttributes(attributesData);
    }
}
