/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * conditionParams 提取器.
 *
 * @author 张越
 * @since 2024-08-06
 */
public class ConditionParamsExtractor implements ValueExtractor {
    @Override
    public Object extract(AttributesData attributesData) {
        JSONObject flowMeta = attributesData.getFlowMetaOrData();
        if (Objects.isNull(flowMeta)) {
            // 兼容原来的逻辑
            return null;
        }
        JSONObject conditionParams = ObjectUtils.cast(flowMeta.get("conditionParams"));
        conditionParams.put("branches", this.getRunnableBranches(conditionParams));
        return conditionParams;
    }

    private JSONArray getRunnableBranches(JSONObject conditionParams) {
        JSONArray branches = ObjectUtils.cast(conditionParams.get("branches"));
        List<Object> runnableBranches = branches.stream().filter(this::isRunnable).collect(Collectors.toList());
        return new JSONArray(runnableBranches);
    }

    private boolean isRunnable(Object branch) {
        JSONObject jsonBranch = ObjectUtils.cast(branch);
        Boolean runnable = ObjectUtils.cast(jsonBranch.get("runnable"));
        return runnable == null || runnable == Boolean.TRUE;
    }
}
