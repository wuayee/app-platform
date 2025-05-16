/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.NodesExtractor;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.Attribute;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.AbstractAttributes;

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
