/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
