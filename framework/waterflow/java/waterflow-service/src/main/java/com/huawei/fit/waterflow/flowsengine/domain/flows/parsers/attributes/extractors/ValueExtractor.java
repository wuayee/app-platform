/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;

/**
 * 属性提取器接口.
 *
 * @author 张越
 * @since 2024-08-05
 */
public interface ValueExtractor {
    /**
     * json 格式数据.
     *
     * @param data 数据.
     * @return 属性对象值.
     */
    Object extract(AttributesData data);
}
