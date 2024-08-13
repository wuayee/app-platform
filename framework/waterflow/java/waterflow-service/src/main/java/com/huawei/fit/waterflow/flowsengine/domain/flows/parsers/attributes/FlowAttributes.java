/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes;

import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.NodesExtractor;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.Attribute;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.AbstractAttributes;

import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.Objects;

/**
 * 流程.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class FlowAttributes extends AbstractAttributes {
    /**
     * 构造函数.
     *
     * @param data 原始数据.
     * @param version 版本号.
     */
    public FlowAttributes(JSONObject data, String version) {
        super(new AttributesData(data));
        this.attributeList.add(new Attribute("name", Collections.singletonList("title")));
        this.attributeList.add(new Attribute("metaId", Collections.singletonList("id")));
        this.attributeList.add(new Attribute("exceptionFitables", Collections.singletonList("exceptionFitables")));
        this.attributeList.add(new Attribute("version", version));
        this.attributeList.add(new Attribute("status", "active"));
        this.attributeList.add(new Attribute("nodes", new NodesExtractor()));

        // 处理graph级别的flowMeta.
        JSONObject flowMeta = super.attributesData.getFlowMeta();
        if (!Objects.isNull(flowMeta)) {
            flowMeta.forEach((key, value) -> this.attributeList.add(new Attribute(key, value)));
        }
    }

    @Override
    public String toString() {
        return new JSONObject(this.getData()).toString();
    }

    @Override
    public boolean isRunnable() {
        throw new UnsupportedOperationException("isRunnable not support in JadeFlow.");
    }
}
