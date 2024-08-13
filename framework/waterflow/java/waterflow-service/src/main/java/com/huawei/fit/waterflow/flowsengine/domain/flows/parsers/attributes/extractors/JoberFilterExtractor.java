/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.Attributes;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.Objects;

/**
 * jobFilter 提取器.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class JoberFilterExtractor implements ValueExtractor {
    @Override
    public Object extract(AttributesData shape) {
        JSONObject flowMeta = shape.getFlowMetaOrData();
        if (!flowMeta.containsKey("jober")) {
            return null;
        }
        if (Objects.isNull(flowMeta.get("joberFilter"))) {
            return null;
        }
        if (Attributes.isState(ObjectUtils.cast(shape.getData().get("type")))) {
            return flowMeta.get("joberFilter");
        }
        return null;
    }
}
