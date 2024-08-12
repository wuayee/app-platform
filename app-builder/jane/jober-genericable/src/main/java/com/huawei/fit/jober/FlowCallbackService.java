/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 流程实例回调函数 Genericable。
 *
 * @author 李哲峰
 * @since 2023-12-11
 */
public interface FlowCallbackService {
    /**
     * 回调函数实现
     *
     * @param contexts 流程上下文信息
     */
    @Genericable(id = "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw")
    void callback(List<Map<String, Object>> contexts);
}
