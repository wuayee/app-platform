/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.Attributes;
import modelengine.fitframework.util.ObjectUtils;

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
