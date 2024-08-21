/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types;

import modelengine.fitframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;

/**
 * 图形接口.
 *
 * @author 张越
 * @since 2024-08-05
 */
public interface Attributes {
    /**
     * 获取数据.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} 键值对.
     */
    Map<String, Object> getData();

    /**
     * 是否可在流程中运行.
     *
     * @return true/false.
     */
    boolean isRunnable();

    /**
     * 判断是否是 state 类型.
     *
     * @param type 类型字符串.
     * @return true/false.
     */
    static boolean isState(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        return "state".equals(type.toLowerCase(Locale.ROOT)) || type.toLowerCase(Locale.ROOT).endsWith("state");
    }
}
