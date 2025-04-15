/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.jadeconfig.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 状态节点入参提取器.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class StateNodeInputParamsExtractor implements InputParamsExtractor {
    @Override
    public JSONArray extract(JSONObject shape) {
        return shape.getJSONObject("flowMeta")
                .getJSONObject("jober")
                .getJSONObject("converter")
                .getJSONObject("entity")
                .getJSONArray("inputParams");
    }
}
