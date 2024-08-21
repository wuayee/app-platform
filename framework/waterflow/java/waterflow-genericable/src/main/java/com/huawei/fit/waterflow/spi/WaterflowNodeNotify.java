/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.spi;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 流程实例回调函数 Genericable。
 *
 * @author 李哲峰
 * @since 1.0
 */
public interface WaterflowNodeNotify {
    /**
     * ON_CONTEXT_COMPLETE_GENERICABLE
     */
    String ON_CONTEXT_COMPLETE_GENERICABLE = "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw";

    /**
     * 回调函数实现
     *
     * @param contexts 流程上下文信息
     */
    @Genericable(id = ON_CONTEXT_COMPLETE_GENERICABLE)
    void onContextComplete(List<Map<String, Object>> contexts);
}
