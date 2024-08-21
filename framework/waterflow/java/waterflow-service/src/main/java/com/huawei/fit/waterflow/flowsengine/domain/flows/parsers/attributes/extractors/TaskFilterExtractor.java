/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.Attributes;
import modelengine.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.Objects;

/**
 * taskFilter 提取器.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class TaskFilterExtractor implements ValueExtractor {
    @Override
    public Object extract(AttributesData attributesData) {
        JSONObject flowMeta = attributesData.getFlowMetaOrData();
        if (!flowMeta.containsKey("task")) {
            return null;
        }
        if (Objects.isNull(flowMeta.get("taskFilter"))) {
            return null;
        }
        if (Attributes.isState(ObjectUtils.cast(attributesData.getData().get("type")))) {
            return flowMeta.get("taskFilter");
        }
        return null;
    }
}
