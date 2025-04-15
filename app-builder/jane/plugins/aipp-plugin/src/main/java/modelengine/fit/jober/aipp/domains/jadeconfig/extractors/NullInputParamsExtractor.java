/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.jadeconfig.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 统一返回null的入参提取器.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class NullInputParamsExtractor implements InputParamsExtractor {
    @Override
    public JSONArray extract(JSONObject shape) {
        return null;
    }
}
