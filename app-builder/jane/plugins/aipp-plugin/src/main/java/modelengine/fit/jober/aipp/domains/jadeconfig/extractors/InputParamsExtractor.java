/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.jadeconfig.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 输入参数提取器.
 *
 * @author 张越
 * @since 2025-01-14
 */
public interface InputParamsExtractor {
    /**
     * 提取.
     *
     * @param shape 图形数据.
     * @return {@link JSONArray} 对象.
     */
    JSONArray extract(JSONObject shape);
}
